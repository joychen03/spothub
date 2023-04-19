package com.itb.dam.jiafuchen.spothub.data.mongodb

import android.util.Log
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.interfaces.IRealmRepository
import com.itb.dam.jiafuchen.spothub.data.mongodb.interfaces.USER_DEFAULT_LIMITS
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

object RealmRepository : IRealmRepository {
    private val currentUser : RealmUser get() = app.currentUser ?: throw IllegalStateException("User must be logged in")
    private lateinit var realm : Realm

    init {
        setup()
    }

    override fun setup() {
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

    override fun getUsers(limit: Int): Flow<List<User>> {

        return realm.query<User>()
            .sort(Pair("_id", Sort.DESCENDING))
            .asFlow().map { it.list }
    }

    override suspend fun addUser(user: User) : User? {

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

    override suspend fun updateUser(user: User) {
        try {
            realm.write {
                copyToRealm(user)
            }
        }catch (e: Exception){
            Log.e("Realm", "Error adding user: ${e.message}")
        }
    }

    override suspend fun deleteUser(user: User) {
        TODO("Not yet implemented")
    }

    override fun getFollowers(limit: Int): Flow<List<User>> {
        TODO("Not yet implemented")
    }

    override fun getUsersByKeyword(keyword: String, limit: Int): Flow<List<User>> {
        TODO("Not yet implemented")
    }

    override fun getUsersByKeywordBeforeDate(
        keyword: String,
        date: Date,
        limit: Int
    ): Flow<List<User>> {
        TODO("Not yet implemented")
    }

    override fun getPosts(limit: Int): Flow<List<Post>> {
        TODO("Not yet implemented")
    }

    override fun getPostsBeforeDate(datetime: Date, limit: Int): Flow<List<Post>> {
        TODO("Not yet implemented")
    }

    override suspend fun addPost(post: Post) : Post? {
        try {
            return realm.write {
                copyToRealm(post)
            }
        }catch (e: Exception){
            Log.e("Realm", "Error adding user: ${e.message}")
            return null
        }
    }

    override suspend fun updatePost(post: Post) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePost(post: Post) {
        TODO("Not yet implemented")
    }

    override fun getFollowingPosts(limit: Int): Flow<List<Post>> {
        TODO("Not yet implemented")
    }

    override fun getFollowingPostsBeforeDate(date: Date, limit: Int): Flow<List<Post>> {
        TODO("Not yet implemented")
    }

    override fun getPostsByKeyword(keyword: String, limit: Int): Flow<List<Post>> {
        TODO("Not yet implemented")
    }

    override fun getPostsByKeywordBeforeDate(
        keyword: String,
        date: Date,
        limit: Int
    ): Flow<List<Post>> {
        TODO("Not yet implemented")
    }


}

