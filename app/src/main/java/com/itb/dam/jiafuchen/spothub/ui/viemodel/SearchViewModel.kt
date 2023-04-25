package com.itb.dam.jiafuchen.spothub.ui.viemodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import com.itb.dam.jiafuchen.spothub.domain.model.Event
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.mongodb.kbson.ObjectId
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(): ViewModel() {
    var searchText : String = ""
    var currentUser : User? = null

    val postUpdated : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val userUpdated : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    var posts = mutableListOf<Post>()
    var users = mutableListOf<User>()


    fun setup(){
        if(app.currentUser != null){
            currentUser = RealmRepository.getMyUser()
        }
    }

    fun postFragmentSetup(){
        val result = RealmRepository.getPostsByKeyword(searchText)
        posts = result.toMutableList()
    }

    fun userFragmentSetup(){
        val result = RealmRepository.getUsersByKeyword(searchText)
        users = result.toMutableList()
    }

    fun getAllUsers(): List<User> {
        return RealmRepository.getAllUsers()
    }

    var postRvScrollOffset: Int = 0
    var postRvScrollPosition: Int = 0

    var userRvScrollOffset: Int = 0
    var userRvScrollPosition: Int = 0

//    fun getPostsByKeyword(keyword : String){
//        val result = RealmRepository.getPostsByKeyword(keyword)
//        posts.postValue(result)
//    }
//
//    fun getUsersByKeyword(keyword : String){
//        val result = RealmRepository.getUsersByKeyword(keyword)
//        users.postValue(result)
//    }


    
}