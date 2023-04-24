package com.itb.dam.jiafuchen.spothub.ui.viemodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.notifications.ObjectChange
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.SingleQueryChange
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(

) : ViewModel() {

    var appJustStarted = true

    private val _currentUser = MutableLiveData<User?>()
    val currentUser : LiveData<User?> = _currentUser

    var lastTotalPostCount = -1

    fun getTotalPosts() : Flow<List<Post>>{
        return if(app.currentUser != null){
            RealmRepository.getPostsAsFlow()
        }else{
            flowOf(emptyList())
        }
    }

    fun getCurrentUser(){
        if(app.currentUser != null){
            viewModelScope.launch {
                val user = RealmRepository.getMyUser()
                println(user?.email)
               _currentUser.postValue(user)
            }
        }
    }

    fun getCurrentUserAsFlow() : Flow<User?>{
        return RealmRepository.getMyUserAsFlow()
    }

    fun removeCurrentUser(){
        _currentUser.postValue(null)
    }


}