package com.itb.dam.jiafuchen.spothub.ui.viemodel

import android.widget.MultiAutoCompleteTextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.AuthRepository
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel(){

    var currentUser : User? = null
    var scrollOffset: Int = 0
    var scrollPosition: Int = 0


    lateinit var newestPostList : MutableList<Post>
    var postList : MutableList<Post> = RealmRepository.getPosts().toMutableList()
    var userList : MutableList<User> = RealmRepository.getAllUsers().toMutableList()

    init {
        if(app.currentUser != null){
            currentUser = RealmRepository.getMyUser()
        }

        RealmRepository.getPostsAsFlow().onEach {
            newestPostList = it.toMutableList()
        }.launchIn(viewModelScope)
    }

    fun getPosts(update : Boolean) {
        if(update){
            postList = RealmRepository.getPosts().toMutableList()
        }
        userList = RealmRepository.getAllUsers().toMutableList()
    }

    fun postListScrollTo(indexItemRV: Int, topViewRV: Int) {
        scrollPosition = indexItemRV
        scrollOffset = topViewRV
    }

    suspend fun postLikeClick(postID: ObjectId, checked: Boolean): Post? {
        return RealmRepository.likePost(currentUser!!._id, postID, checked)
    }

    suspend fun followClicked(userID : ObjectId) : User? {
        val userUpdated = RealmRepository.userAddFollower(userID, currentUser!!._id)
        if(userUpdated != null){
            RealmRepository.userAddFollowing(currentUser!!._id, userID)
        }
        return userUpdated
    }


}