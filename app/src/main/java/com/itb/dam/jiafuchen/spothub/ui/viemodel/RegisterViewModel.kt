package com.itb.dam.jiafuchen.spothub.ui.viemodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.util.Util
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.TAG
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.AuthRepository
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import com.itb.dam.jiafuchen.spothub.domain.model.User
import com.itb.dam.jiafuchen.spothub.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.mongodb.ext.customDataAsBsonDocument
import io.realm.kotlin.mongodb.ext.profileAsBsonDocument
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor (

) : ViewModel() {
    private val _errorMsg = MutableLiveData<String>()
    val errorMsg : LiveData<String> = _errorMsg

    private val _loggedIn = MutableLiveData<Boolean>()
    val loggedIn : LiveData<Boolean> = _loggedIn

    private val _appUserCreated = MutableLiveData<User?>()
    val appUserCreated : LiveData<User?> = _appUserCreated

    fun register(email : String, password: String){

        viewModelScope.launch {
            runCatching {
                AuthRepository.createAccount(email,password)
                AuthRepository.login(email,password)
                RealmRepository.setup()
            }.onSuccess {
                _loggedIn.postValue(true)
            }.onFailure {
                _errorMsg.postValue(it.message)
            }
        }

    }

    fun createAppUser(user : User){
        viewModelScope.launch {
            if(app.currentUser != null){
                val user = RealmRepository.addUser(user)
                _appUserCreated.postValue(user)
            }
        }
    }
}