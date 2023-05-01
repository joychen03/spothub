package com.itb.dam.jiafuchen.spothub.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.dam.jiafuchen.spothub.TAG
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.AuthRepository
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(

) : ViewModel(){

    val errorMsg : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val loggedIn : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun login(email : String, password: String){
        viewModelScope.launch {
            runCatching {
                AuthRepository.login(email,password)
                RealmRepository.setup()
            }.onSuccess {
                loggedIn.postValue(true)
                Log.v(TAG(),"Successfully logged in${app.currentUser?.id}")
            }.onFailure {
                errorMsg.postValue(it.message)
            }
        }

    }
}