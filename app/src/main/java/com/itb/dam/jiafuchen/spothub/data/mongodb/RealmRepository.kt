package com.itb.dam.jiafuchen.spothub.data.mongodb

import android.util.Log
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.interfaces.IRealmRepository
import com.itb.dam.jiafuchen.spothub.data.mongodb.interfaces.USER_DEFAULT_LIMITS
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.subscriptions

import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.RealmQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

object RealmRepository : IRealmRepository {
    private val currentUser = app.currentUser
    private lateinit var realm : Realm

    init {
        requireNotNull(currentUser)
        setup()
    }

    override fun setup() {
        if(currentUser != null){
            val config = SyncConfiguration.Builder(
                currentUser,
                setOf(User::class, Post::class)
            ).initialSubscriptions{ sub ->
                add(query = sub.query<Post>())
            }
                .waitForInitialRemoteData()
                .build()

            realm = Realm.open(config)
            Log.v("Realm","Successfully opened realm: ${realm.configuration.name}")

            CoroutineScope(Dispatchers.Main).launch {
                realm.subscriptions.waitForSynchronization()
            }
        }

    }

    override fun getUsers(limit: Int): Flow<List<User>> {
        TODO("Not yet implemented")
    }

    override fun addUser(user: User) {
        TODO("Not yet implemented")
    }

    override fun updateUser(user: User) {
        TODO("Not yet implemented")
    }

    override fun deleteUser(user: User) {
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

    override fun addPost(post: Post) {
        TODO("Not yet implemented")
    }

    override fun updatePost(post: Post) {
        TODO("Not yet implemented")
    }

    override fun deletePost(post: Post) {
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

