package com.itb.dam.jiafuchen.spothub.data.mongodb.interfaces

import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow
import java.util.*

const val POST_DEFAULT_LIMITS = 100
const val USER_DEFAULT_LIMITS = 100

interface IRealmRepository {
    fun setup()

    fun getUsers(limit : Int = USER_DEFAULT_LIMITS) : Flow<List<User>>
    fun addUser(user : User)
    fun updateUser(user : User)
    fun deleteUser(user : User)
    fun getFollowers(limit : Int = USER_DEFAULT_LIMITS) : Flow<List<User>>
    fun getUsersByKeyword(keyword : String, limit: Int = USER_DEFAULT_LIMITS) : Flow<List<User>>
    fun getUsersByKeywordBeforeDate(keyword : String, date: Date ,limit: Int = USER_DEFAULT_LIMITS) : Flow<List<User>>

    fun getPosts(limit : Int = POST_DEFAULT_LIMITS) : Flow<List<Post>>
    fun getPostsBeforeDate(datetime : Date, limit: Int = POST_DEFAULT_LIMITS) : Flow<List<Post>>
    fun addPost(post : Post)
    fun updatePost(post : Post)
    fun deletePost(post : Post)
    fun getFollowingPosts(limit : Int = 100) : Flow<List<Post>>
    fun getFollowingPostsBeforeDate(date : Date, limit : Int = POST_DEFAULT_LIMITS) : Flow<List<Post>>
    fun getPostsByKeyword(keyword : String, limit: Int = POST_DEFAULT_LIMITS) : Flow<List<Post>>
    fun getPostsByKeywordBeforeDate(keyword: String, date : Date, limit : Int = POST_DEFAULT_LIMITS) : Flow<List<Post>>

}
