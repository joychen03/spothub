package com.itb.dam.jiafuchen.spothub.ui.viemodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor() : ViewModel() {

    var currentUser: User? = null
    var currentPost : Post? = null

    var title : String? = null
    var description : String? = null
    var image : Uri? = null
    var location : LatLng? = null

    val errorMessage : MutableLiveData<String?> by lazy {
        MutableLiveData<String?>()
    }

    val onUpdated : MutableLiveData<Post> by lazy {
        MutableLiveData<Post>()
    }

    val onDeleted : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    init {
        currentUser = RealmRepository.getMyUser()
    }

    fun updatePost(postID : ObjectId, post : Post){
        viewModelScope.launch{
            try{
                val updatedPost = RealmRepository.updatePost(postID, post) ?: throw Exception("Update failed : Unkown error")
                onUpdated.postValue(updatedPost)
            }catch (e : Exception){
                errorMessage.postValue("Update failed : \n\n ${e.message}")
            }
        }
    }

    fun clearAll() {
        this.title = null
        this.description = null
        this.image = null
        this.location = null
        this.currentPost = null
    }

    fun deletePost() {
        viewModelScope.launch{
            try{
                val deleted = RealmRepository.deletePost(currentPost!!._id)
                if(!deleted) throw Exception("Delete error : Unkown error")

                onDeleted.postValue(true)
            }catch (e : Exception){
                errorMessage.postValue("Delete error : \n\n ${e.message}")
            }
        }
    }


}