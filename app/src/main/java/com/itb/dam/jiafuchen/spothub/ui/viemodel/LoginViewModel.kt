package com.itb.dam.jiafuchen.spothub.ui.viemodel

import android.text.BoringLayout
import android.util.Log
import androidx.lifecycle.LiveData
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

    private val _errorMsg = MutableLiveData<String>()
    val errorMsg : LiveData<String> = _errorMsg

    private val _loggedIn = MutableLiveData<Boolean>()
    val loggedIn : LiveData<Boolean> = _loggedIn

    fun login(email : String, password: String){
        viewModelScope.launch {
            runCatching {
                AuthRepository.login(email,password)
                RealmRepository.setup()
            }.onSuccess {
                _loggedIn.postValue(true)
                Log.v(TAG(),"Successfully logged in${app.currentUser?.id}")
            }.onFailure {
                _errorMsg.postValue(it.message)
            }
        }

    }
}