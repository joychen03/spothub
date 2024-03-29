package com.itb.dam.jiafuchen.spothub.data.mongodb

import android.util.Log
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import io.realm.kotlin.mongodb.User as RealmUser

object RealmRepository {
    private val currentUser : RealmUser get() = app.currentUser ?: throw IllegalStateException("User must be logged in")
    private lateinit var realm : Realm

    init {
        setup()
    }

    fun setup() {
        val config = SyncConfiguration.Builder(
            currentUser,
            setOf(User::class, Post::class)
        ).initialSubscriptions{ sub ->
            add(query = sub.query<Post>())
            add(query = sub.query<User>())
        }
            .waitForInitialRemoteData()
            .build()

        realm = Realm.open(config)
        Log.v("Realm","Successfully opened realm: ${realm.configuration.name}")
    }

    suspend fun addUser(user: User) : User? {
        try{
            return realm.write {
                return@write copyToRealm(user)
            }
        }catch (e: Exception){
            Log.e("Realm", "Error adding user: ${e.message}")
            return null
        }
    }

    fun getMyUser() : User? {
        return realm.query<User>("owner_id == $0", currentUser.id).find().firstOrNull()
    }

    fun getUserById(id : ObjectId) : User? {
        return realm.query<User>("_id == $0", id).find().firstOrNull()
    }

    fun getUserByOwnerIdAsFlow(ownerID : String) : Flow<User?> {
        return realm.query<User>("owner_id == $0", ownerID).first().asFlow().map { it.obj }
    }

    fun getUserByIdAsFlow(userID : ObjectId) : Flow<User?> {
        return realm.query<User>("_id == $0", userID).first().asFlow().map { it.obj }
    }

    suspend fun updateUser(userID : ObjectId, user: User) : User?{
        return try {
            realm.write {
                val userToUpdate = this.query<User>("_id == $0", userID).first().find() ?: return@write null

                if(user.username.isNotEmpty()){
                    userToUpdate.username = user.username
                }
                userToUpdate.description = user.description
                if(user.avatar.isNotEmpty()){
                    userToUpdate.avatar = user.avatar
                }

                userToUpdate.updateDataTime = RealmInstant.now()

                userToUpdate
            }

        }catch (e: Exception){
            Log.e("Realm", "Error adding user: ${e.message}")
            null
        }
    }

    fun getMyFollowers(myUser : User): List<User> {
        return realm.query<User>("_id in {$0}", myUser.followers.joinToString(",")).find()
    }

    fun getMyFollowings(myUser : User): List<User> {
        return realm.query<User>("_id in {$0}", myUser.followings.joinToString(",")).find()
    }

    fun getUsersByKeyword(keyword: String): List<User> {
        return realm.query<User>("username CONTAINS[c] $0", keyword).find()
    }

    fun getPosts(): List<Post> {
        return realm.query<Post>()
            .sort(Pair("_id", Sort.DESCENDING))
            .find()
    }

    fun getPostsAsFlow() : Flow<ResultsChange<Post>>{
        return realm.query(Post::class).asFlow()
    }

    fun getAllUsersAsFlow(): Flow<ResultsChange<User>> {
        return realm.query(User::class).asFlow()
    }

    fun getMyPostsAsFlow(): Flow<ResultsChange<Post>> {
        return realm.query<Post>("owner_id == $0", currentUser.id).asFlow()
    }

    fun getPostsAsFlowOLD(): Flow<List<Post>> {
        return realm.query<Post>()
            .sort(Pair("_id", Sort.DESCENDING))
            .asFlow().map { it.list }
    }

    suspend fun addPost(post: Post) : Post? {
        return try {
            realm.write {
                copyToRealm(post)
            }
        }catch (e: Exception){
            Log.e("Realm", "Error adding post: ${e.message}")
            null
        }
    }

    suspend fun updatePost(postID : ObjectId, post: Post) : Post? {
        return try {
            realm.write {
                val postToUpdate = this.query<Post>("_id == $0", postID).first().find() ?: return@write null

                if(post.title.isNotEmpty()) postToUpdate.title = post.title
                postToUpdate.description = post.description
                if (post.image.isNotEmpty()) postToUpdate.image = post.image
                postToUpdate.latitude = post.latitude
                postToUpdate.longitude = post.longitude
                postToUpdate.updateDataTime = RealmInstant.now()

                postToUpdate
            }
        }catch (e: Exception){
            Log.e("Realm", "Error updating post: ${e.message}")
            null
        }
    }

    suspend fun deletePost(postID : ObjectId) : Boolean{
        return try {
            realm.write {
                val postToDelete = this.query<Post>("_id == $0", postID).first().find() ?: return@write false
                delete(postToDelete)
                true
            }
        }catch (e: Exception){
            Log.e("Realm", "Error deleting post: ${e.message}")
            false
        }
    }

    fun getFollowingPosts(following : RealmList<ObjectId>): List<Post> {
        return realm.query<Post>("owner_id in {$0}", following.joinToString(",")).find()
    }

    fun getPostsByKeyword(keyword: String): List<Post> {
        return realm.query<Post>("title CONTAINS[c] $0", keyword)
            .sort(Pair("_id", Sort.DESCENDING))
            .find()
    }

    fun getAllUsers(): List<User> {
        return realm.query<User>().find()
    }

    suspend fun likePost(userID : ObjectId, postID : ObjectId) : Post?{
        return try {
            realm.write {
                 val postToUpdate = this.query<Post>("_id == $0", postID).first().find()
                 postToUpdate?.let {
                    if(!it.likes.contains(userID)){
                        it.likes.add(userID)
                    }
                 }
                postToUpdate
            }

        }catch (e: Exception){
            Log.e("Realm", "Error liking post: ${e.message}")
            null
        }
    }

    suspend fun unLikePost(userID : ObjectId, postID : ObjectId) : Post?{
        return try {
            realm.write {
                val postToUpdate = this.query<Post>("_id == $0", postID).first().find()
                postToUpdate?.let {
                    it.likes.remove(userID)
                }
                postToUpdate
            }

        }catch (e: Exception){
            Log.e("Realm", "Error liking post: ${e.message}")
            null
        }
    }

    suspend fun userAddFollower(userID : ObjectId, followerID: ObjectId) : User? {
        return try {
            realm.write {
                val userToUpdate = this.query<User>("_id == $0", userID).first().find()
                userToUpdate?.let {
                    if(!it.followers.contains(followerID)){
                        it.followers.add(followerID)
                    }
                }
                userToUpdate
            }
        }catch (e: Exception){
            Log.e("Realm", "Error adding follower user: ${e.message}")
            null
        }

    }

    suspend fun userAddFollowing(userID : ObjectId, followingID : ObjectId) : User?{
        return try {
            realm.write {
                val userToUpdate = this.query<User>("_id == $0", userID).first().find()
                userToUpdate?.let {
                    if(!it.followings.contains(followingID)){
                        it.followings.add(followingID)
                    }
                }
                userToUpdate
            }
        }catch (e: Exception){
            Log.e("Realm", "Error adding following user: ${e.message}")
            null
        }
    }

    suspend fun userRemoveFollower(userID : ObjectId, followerID: ObjectId) : User? {
        return try {
            realm.write {
                val userToUpdate = this.query<User>("_id == $0", userID).first().find()
                userToUpdate?.let {
                    it.followers.remove(followerID)
                }
                userToUpdate
            }
        }catch (e: Exception){
            Log.e("Realm", "Error adding follower user: ${e.message}")
            null
        }

    }

    suspend fun userRemoveFollowing(userID : ObjectId, followingID : ObjectId) : User?{
        return try {
            realm.write {
                val userToUpdate = this.query<User>("_id == $0", userID).first().find()
                userToUpdate?.let {
                    it.followings.remove(followingID)
                }
                userToUpdate
            }
        }catch (e: Exception){
            Log.e("Realm", "Error adding following user: ${e.message}")
            null
        }
    }

    fun getUserPosts(ownerID : String): List<Post> {
        return realm.query<Post>("owner_id == $0", ownerID)
            .sort(Pair("_id", Sort.DESCENDING))
            .find()
    }

    fun getMyPosts(ownerID : String): List<Post> {
        return realm.query<Post>("owner_id == $0", ownerID)
            .sort(Pair("_id", Sort.DESCENDING))
            .find()
    }

    fun getMyFavPosts(userID : ObjectId): List<Post> {
        return realm.query<Post>("$0 in likes", userID).find()
    }

    fun getPostByIdAsFlow(postId: ObjectId): Flow<Post?> {
        return realm.query<Post>("_id == $0", postId).first().asFlow().map { it.obj }
    }


}

