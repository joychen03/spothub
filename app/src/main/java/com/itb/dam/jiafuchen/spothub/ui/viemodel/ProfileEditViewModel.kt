package com.itb.dam.jiafuchen.spothub.ui.viemodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import com.itb.dam.jiafuchen.spothub.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor() : ViewModel() {
    var currentUser: User? = null

    var username : String? = null
    var description : String? = null
    var avatar : Uri? = null

    val errorMessage : MutableLiveData<String?> by lazy {
        MutableLiveData<String?>()
    }

    val onUpdated : MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    init {
        currentUser = RealmRepository.getMyUser()
    }

    fun updateUser(user : User){
        viewModelScope.launch{
            try{
                val updatedUser = RealmRepository.updateUser(currentUser!!._id, user) ?: throw Exception("Unkown error")
                currentUser = updatedUser
                onUpdated.postValue(updatedUser)
            }catch (e : Exception){
                errorMessage.postValue(e.message)
            }
        }
    }

    fun goBack(){
        username = null
        description = null
        avatar = null
    }
}