package com.itb.dam.jiafuchen.spothub.ui.viemodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import com.itb.dam.jiafuchen.spothub.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(

) : ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser : LiveData<User?> = _currentUser

    init {
        getCurrentUser()
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