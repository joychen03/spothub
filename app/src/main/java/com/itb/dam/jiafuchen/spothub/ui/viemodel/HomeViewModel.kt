package com.itb.dam.jiafuchen.spothub.ui.viemodel

import android.widget.MultiAutoCompleteTextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.AuthRepository
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel(){

    var currentUser : User? = null
    var scrollOffset: Int = 0
    var scrollPosition: Int = 0
    var firstInit = true

    var postList : MutableList<Post> = mutableListOf()
    var userList : MutableList<User> = mutableListOf()
    lateinit var job : Job


    val postAdded : MutableLiveData<Post> by lazy {
        MutableLiveData<Post>()
    }

    val postUpdated : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val datasetUpdated : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }


    fun setup(update: Boolean){
        currentUser = RealmRepository.getMyUser()

        if(currentUser == null){
            return
        }

        if(!update && !firstInit){
           return
        }

        postList = RealmRepository.getPosts().toMutableList()
        userList = RealmRepository.getAllUsers().toMutableList()
        firstInit = false

        job = viewModelScope.launch {
            RealmRepository.getPostsAsFlowTest().collect { changes: ResultsChange<Post> ->
                when (changes) {
                    is UpdatedResults -> {
                        if(changes.insertions.isNotEmpty()){
                            postAdded.postValue(changes.list[changes.insertions[0]])
                        }else if(changes.changes.isNotEmpty()){
                            for(index in changes.changes) {
                                val updatedPost = changes.list[index]
                                val indexToUpdate = postList.indexOfFirst { it._id == updatedPost._id }
                                if(indexToUpdate != -1){
                                    postList[indexToUpdate] = updatedPost
                                    postUpdated.postValue(indexToUpdate)
                                }
                            }
                        }
                    }
                    else -> {
                        println("IGNORING")
                    }
                }
            }

            RealmRepository.getAllUsersAsFlow().collect { changes: ResultsChange<User> ->
                when (changes) {
                    is UpdatedResults -> {
                        if(changes.insertions.isNotEmpty()){
                            datasetUpdated.postValue(true)
                        }else if(changes.changes.isNotEmpty()){
                            for(index in changes.changes) {
                                val updatedUser = changes.list[index]
                                val indexToUpdate = userList.indexOfFirst { it._id == updatedUser._id }
                                if(indexToUpdate != -1){
                                    userList[indexToUpdate] = updatedUser
                                    datasetUpdated.postValue(true)
                                }
                            }
                        }
                    }
                    else -> {
                        println("IGNORING")
                    }
                }
            }
        }


    }

    fun getPosts(update : Boolean) {
        if(update){
            postList = RealmRepository.getPosts().toMutableList()
        }
        userList = RealmRepository.getAllUsers().toMutableList()
    }

    fun postListScrollTo(indexItemRV: Int, topViewRV: Int) {
        scrollPosition = indexItemRV
        scrollOffset = topViewRV
    }


}