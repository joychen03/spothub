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

    val posts : MutableLiveData<List<Post>> by lazy {
        MutableLiveData<List<Post>>()
    }
    val users : MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>()
    }

    var currentUser : User? = null

    init {
        currentUser = RealmRepository.getMyUser()
    }

    var postRvScrollOffset: Int = 0
    var postRvScrollPosition: Int = 0

    var userRvScrollOffset: Int = 0
    var userRvScrollPosition: Int = 0

    fun getPostsByKeyword(keyword : String){
        val result = RealmRepository.getPostsByKeyword(keyword)
        posts.postValue(result)
    }

    fun getUsersByKeyword(keyword : String){
        val result = RealmRepository.getUsersByKeyword(keyword)
        users.postValue(result)
    }
    
}