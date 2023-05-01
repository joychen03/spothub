package com.itb.dam.jiafuchen.spothub.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(

) : ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser : LiveData<User?> = _currentUser

    var homeShouldUpdate = false

    val newPost : MutableLiveData<Post> by lazy {
        MutableLiveData<Post>()
    }

    val homeUpdated : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    init {
        getCurrentUser()
    }


    fun getCurrentUser(){
        if(app.currentUser != null){
            viewModelScope.launch {
                val user = RealmRepository.getMyUser()
               _currentUser.postValue(user)
            }
        }else{
            _currentUser.postValue(null)
        }
    }

    fun getCurrentUserAsFlow() : Flow<User?>{
        return RealmRepository.getUserByOwnerIdAsFlow(app.currentUser!!.id)
    }

    fun removeCurrentUser(){
        _currentUser.postValue(null)
    }

    fun homeScreenUpdated(){
        homeUpdated.postValue(true)
    }

    suspend fun likePost(postID: ObjectId): Post? {
        return RealmRepository.likePost(currentUser.value!!._id, postID)
    }

    suspend fun unlikePost(postID: ObjectId): Post? {
        return RealmRepository.unLikePost(currentUser.value!!._id, postID)
    }

    suspend fun addFollower(userID : ObjectId) : User? {
        val userUpdated = RealmRepository.userAddFollower(userID, currentUser.value!!._id)
        if(userUpdated != null){
            RealmRepository.userAddFollowing(currentUser.value!!._id, userID)
        }
        return userUpdated
    }

    suspend fun removeFollower(userID: ObjectId) : User? {
        val updatedUser = RealmRepository.userRemoveFollower(userID, currentUser.value!!._id)
        if(updatedUser != null){
            RealmRepository.userRemoveFollowing(currentUser.value!!._id, userID)
        }
        return updatedUser
    }

    fun watchForNewPosts(){
        viewModelScope.launch {
            RealmRepository.getPostsAsFlow().collect { changes: ResultsChange<Post> ->
                when (changes) {
                    is UpdatedResults -> {
                        if (changes.insertions.isNotEmpty()) {
                            val post = changes.list[changes.insertions[0]]
                            newPost.postValue(post)
                        }
                    }
                    else -> {}
                }
            }
        }
    }


}