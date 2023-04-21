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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(

) : ViewModel() {

    var appJustStarted = true

    private val _currentUser = MutableLiveData<User?>()
    val currentUser : LiveData<User?> = _currentUser

    init {
        getCurrentUser()
    }

    fun getTotalPosts() : Flow<List<Post>>{
        return RealmRepository.gePostsAsFlow()
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





}