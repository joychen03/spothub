package com.itb.dam.jiafuchen.spothub.data.mongodb

import android.util.Log
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import io.realm.kotlin.mongodb.User as RealmUser
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncClientResetStrategy

import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.util.*

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

        CoroutineScope(Dispatchers.Main).launch {
            realm.subscriptions.waitForSynchronization()
        }
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

    suspend fun updateUser(user: User) {
        try {
            realm.write {
                copyToRealm(user)
            }
        }catch (e: Exception){
            Log.e("Realm", "Error adding user: ${e.message}")
        }
    }

    fun getMyFollowers(myUser : User): List<User> {
        return realm.query<User>("_id in {$0}", myUser.followers.joinToString(",")).find()
    }

    fun getMyFollowings(myUser : User): List<User> {
        return realm.query<User>("_id in {$0}", myUser.followings.joinToString(",")).find()
    }

    fun getUsersByKeyword(keyword: String): List<User> {
        return realm.query<User>("username contains $0", keyword).find()
    }

    fun getPosts(limit: Int): List<Post> {
        return realm.query<Post>()
            .sort(Pair("_id", Sort.DESCENDING))
            .find()
    }

    fun getMyPostsAsFlow(): Flow<List<Post>> {
        return realm.query<Post>("owner_id == $0", currentUser.id)
            .sort(Pair("_id", Sort.DESCENDING))
            .asFlow().map { it.list }
    }

    fun getTotalPosts() : Flow<Long> {
        return realm.query<Post>().count().asFlow()
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

    suspend fun updatePost(post: Post) : Post? {
        return try {
            realm.write {
                val postToUpdate = realm.query<Post>("_id == $0", post._id).find().firstOrNull()
                postToUpdate?.let {
                    it.title = post.title
                    it.description = post.description
                    it.image = post.image
                    it.latitude = post.latitude
                    it.longitude = post.longitude
                    it.updateDataTime = post.updateDataTime
                }
                postToUpdate
            }
        }catch (e: Exception){
            Log.e("Realm", "Error updating post: ${e.message}")
            null
        }
    }

    suspend fun deletePost(post: Post) : Boolean{
        return try {
            realm.write {
                val postToDelete = realm.query<Post>("_id == $0", post._id).find().firstOrNull()
                postToDelete?.let {
                    delete(it)
                }
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
        return realm.query<Post>("title contains $0 or description ", keyword).find()
    }



}

