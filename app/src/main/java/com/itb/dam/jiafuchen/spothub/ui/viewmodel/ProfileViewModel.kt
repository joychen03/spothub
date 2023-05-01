package com.itb.dam.jiafuchen.spothub.ui.viewmodel

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(): ViewModel() {


    var selectedTab: Int = 0
    var currentUser : User? = null
    var favPosts = mutableListOf<Post>()
    var myPosts = mutableListOf<Post>()
    var users = mutableListOf<User>()

    val favPostsChanged : MutableLiveData<List<Int>> by lazy {
        MutableLiveData<List<Int>>()
    }
    val favPostsAdded : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val favPostsRemoved : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val myPostsChanged : MutableLiveData<List<Int>> by lazy {
        MutableLiveData<List<Int>>()
    }
    val myPostDeleted : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val userClick : MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }
    val postClick : MutableLiveData<Post> by lazy {
        MutableLiveData<Post>()
    }


    init {
        if(app.currentUser != null){
            subscribeChanges()
            currentUser = RealmRepository.getMyUser()
        }
    }

    fun setup(){
        favPosts = RealmRepository.getMyFavPosts(currentUser!!._id).toMutableList().asReversed()
        users = RealmRepository.getAllUsers().toMutableList()
        myPosts = RealmRepository.getMyPosts(app.currentUser!!.id).toMutableList()
    }


    fun goToUser(user: User) {
        userClick.postValue(user)
    }

    fun goToPost(post: Post) {
        postClick.postValue(post)
    }

    private fun subscribeChanges(){
        viewModelScope.launch {
            RealmRepository.getPostsAsFlow().collect { changes: ResultsChange<Post> ->
                if (changes is UpdatedResults) {
                    for (i in changes.changes) {
                        val updatedPost = changes.list[i]
                        val indexToUpdate = favPosts.indexOfFirst { it._id == updatedPost._id }

                        if(updatedPost.likes.contains(currentUser?._id)){
                            if (indexToUpdate != -1) {
                                favPosts[indexToUpdate] = updatedPost
                                favPostsChanged.postValue(listOf(indexToUpdate))
                            }else{
                                favPosts.add(0, updatedPost)
                                favPostsAdded.postValue(0)
                            }
                        }else{
                            if (indexToUpdate != -1) {
                                favPosts.removeAt(indexToUpdate)
                                favPostsRemoved.postValue(indexToUpdate)
                            }
                        }

                    }
                }
            }
        }

        viewModelScope.launch {
            RealmRepository.getMyPostsAsFlow().collect{ changes: ResultsChange<Post> ->
                if (changes is UpdatedResults) {
                    for (i in changes.insertions) {
                        val addedPost = changes.list[i]
                        myPosts.add(0, addedPost)
                        myPostsChanged.postValue(listOf(0))
                    }

                    for (i in changes.deletions) {
                        myPosts = changes.list.toMutableList()
                        myPostDeleted.postValue(true)

                    }
                    for (i in changes.changes) {
                        val updatedPost = changes.list[i]
                        val indexToUpdate = myPosts.indexOfFirst { it._id == updatedPost._id }
                        if (indexToUpdate != -1) {
                            myPosts[indexToUpdate] = updatedPost
                            myPostsChanged.postValue(listOf(indexToUpdate))
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            RealmRepository.getAllUsersAsFlow().collect { changes: ResultsChange<User> ->
                if (changes is UpdatedResults && changes.changes.isNotEmpty()) {
                    for (i in changes.changes) {
                        val updatedUser = changes.list[i]
                        val indexToUpdate = users.indexOfFirst { it._id == updatedUser._id }
                        if (indexToUpdate != -1) {
                            users[indexToUpdate] = updatedUser
                            val postIndexesToUpdate = favPosts.mapIndexedNotNull { index, post ->
                                if (post.owner_id == updatedUser.owner_id) {
                                    index
                                } else {
                                    null
                                }
                            }

                            if (postIndexesToUpdate.isNotEmpty()) {
                                favPostsChanged.postValue(postIndexesToUpdate)
                            }
                        }
                    }
                }
            }
        }
    }

}