package com.itb.dam.jiafuchen.spothub.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor() : ViewModel() {

    var scrollOffset: Int = 0
    var scrollPosition: Int = 0

    val user : MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    val userPosts : MutableLiveData<MutableList<Post>> by lazy {
        MutableLiveData<MutableList<Post>>()
    }

    var currentUser : User? = null

    init {
        currentUser = RealmRepository.getMyUser()
    }


    fun getUser(id : ObjectId){
        viewModelScope.launch {
            RealmRepository.getUserByIdAsFlow(id).collect{
                user.postValue(it)
            }
        }
    }

    fun getPosts(ownerID : String){
        val postsList = RealmRepository.getUserPosts(ownerID).toMutableList()
        userPosts.postValue(postsList)
    }

    fun updatePost(position: Int, updatedPost: Post) {
        userPosts.value?.set(position, updatedPost)
        userPosts.postValue(userPosts.value)
    }

    fun postListScrollTo(indexItemRV: Int, topViewRV: Int) {
        scrollPosition = indexItemRV
        scrollOffset = topViewRV
    }

}