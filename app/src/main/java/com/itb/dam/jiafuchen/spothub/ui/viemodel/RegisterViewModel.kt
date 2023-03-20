package com.itb.dam.jiafuchen.spothub.ui.viemodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.dam.jiafuchen.spothub.TAG
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor (

) : ViewModel() {
    private val _errorMsg = MutableLiveData<String>()
    val errorMsg : LiveData<String> = _errorMsg

    private val _loggedIn = MutableLiveData<Boolean>()
    val loggedIn : LiveData<Boolean> = _loggedIn

    fun register(email : String, password: String){
        viewModelScope.launch {
            runCatching {
                AuthRepository.createAccount(email,password)
                AuthRepository.login(email,password)
            }.onSuccess {
                _loggedIn.postValue(true)
                Log.v(TAG(), "Logged IN im ${app.currentUser?.provider?.name}")
            }.onFailure {
                _errorMsg.postValue(it.message)
            }
        }

    }
}