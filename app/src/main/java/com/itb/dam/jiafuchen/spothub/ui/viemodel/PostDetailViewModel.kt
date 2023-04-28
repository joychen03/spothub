package com.itb.dam.jiafuchen.spothub.ui.viemodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(): ViewModel() {

    var currentUser : User? = null

    val userUpdated : MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    val postUpdated : MutableLiveData<Post> by lazy {
        MutableLiveData<Post>()
    }

    init {
        if(app.currentUser != null){
            currentUser = RealmRepository.getMyUser()
        }
    }

    fun setUp(post : Post){
        viewModelScope.launch {
            RealmRepository.getPostByIdAsFlow(post._id).collect{
                postUpdated.postValue(it)
            }
        }
        viewModelScope.launch {
            RealmRepository.getUserByOwnerIdAsFlow(post.owner_id).collect{
                userUpdated.postValue(it)
            }
        }
    }

}