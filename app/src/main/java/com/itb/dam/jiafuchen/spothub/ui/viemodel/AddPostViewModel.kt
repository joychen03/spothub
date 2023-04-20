package com.itb.dam.jiafuchen.spothub.ui.viemodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.util.Util
import com.google.android.gms.maps.model.LatLng
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(): ViewModel() {

    var title : String = ""
    var description : String = ""
    var image : Uri? = null
    var location : LatLng = LatLng(0.0, 0.0)

    suspend fun publishPost(post : Post) : Post? {
        return try {
            val newPost = RealmRepository.addPost(post)
            newPost
        }catch (e: Exception){
            null
        }
    }

}