package com.itb.dam.jiafuchen.spothub.ui.viemodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel(){

    var currentUser : User? = null
    var scrollOffset: Int = 0
    var scrollPosition: Int = 0
    var firstInit = true

    var postList : MutableList<Post> = mutableListOf()
    var userList : MutableList<User> = mutableListOf()

    val postAdded : MutableLiveData<Post?> by lazy {
        MutableLiveData<Post?>()
    }

    val postUpdated : MutableLiveData<List<Int>> by lazy {
        MutableLiveData<List<Int>>()
    }

    init {
        if(app.currentUser != null){
            subscribeChanges()
        }
    }

    fun setup(update: Boolean){

        if(app.currentUser != null){
            currentUser = RealmRepository.getMyUser()
        }

        if(!update && !firstInit){
           return
        }

        postList = RealmRepository.getPosts().toMutableList()
        userList = RealmRepository.getAllUsers().toMutableList()

        firstInit = false
    }

    private fun subscribeChanges() {

        viewModelScope.launch {
            RealmRepository.getPostsAsFlow().collect { changes: ResultsChange<Post> ->
                when (changes) {
                    is UpdatedResults -> {
                        if (changes.insertions.isNotEmpty()) {
                            postAdded.postValue(changes.list[changes.insertions[0]])

                        } else if (changes.changes.isNotEmpty()) {
                            for (index in changes.changes) {
                                val updatedPost = changes.list[index]
                                val indexToUpdate = postList.indexOfFirst { it._id == updatedPost._id }
                                if (indexToUpdate != -1) {
                                    postList[indexToUpdate] = updatedPost
                                    postUpdated.postValue(listOf(indexToUpdate))
                                }
                            }
                        }
                    }
                    else -> {

                    }
                }
            }
        }

        viewModelScope.launch {
            RealmRepository.getAllUsersAsFlow().collect { changes: ResultsChange<User> ->
                when (changes) {
                    is UpdatedResults -> {
                        if(changes.insertions.isNotEmpty()){
                            for(i in changes.insertions){
                                userList.add(changes.list[i])
                            }
                        }else if(changes.changes.isNotEmpty()){
                            for(index in changes.changes) {
                                val updatedUser = changes.list[index]
                                val indexToUpdate = userList.indexOfFirst { it._id == updatedUser._id }
                                if(indexToUpdate != -1){
                                    userList[indexToUpdate] = updatedUser

                                    val postIndexesToUpdate = postList.filter { it.owner_id == updatedUser.owner_id }.map { postList.indexOf(it) }
                                    postUpdated.postValue(postIndexesToUpdate)
                                }
                            }
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    fun postListScrollTo(indexItemRV: Int, topViewRV: Int) {
        scrollPosition = indexItemRV
        scrollOffset = topViewRV
    }


}