package com.itb.dam.jiafuchen.spothub.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(): ViewModel() {

    var title : String = ""
    var description : String = ""
    var image : Uri? = null
    var location : LatLng = LatLng(0.0, 0.0)


    val posted : MutableLiveData<Post?> by lazy {
        MutableLiveData<Post?>()
    }

    val errorMessage : MutableLiveData<String?> by lazy {
        MutableLiveData<String?>(null)
    }

    fun publishPost(post : Post) {
        viewModelScope.launch {
            try {
                val newPost = RealmRepository.addPost(post) ?: throw Exception("Unknown error")
                posted.postValue(newPost)
            }catch (e: Exception){
                posted.postValue(null)
                errorMessage.postValue(e.message)
            }
        }

    }

    fun clearPost() {
        this.title = ""
        this.description = ""
        this.image = null
        this.location = LatLng(0.0, 0.0)
    }

}