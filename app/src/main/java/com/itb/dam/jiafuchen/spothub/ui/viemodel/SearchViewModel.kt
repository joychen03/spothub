package com.itb.dam.jiafuchen.spothub.ui.viemodel

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
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(): ViewModel() {

    var selectedTab: Int = 0
    var searchText : String = ""
    var currentUser : User? = null

    var postListLastPositon: Int = 0
    var postListOffset : Int = 0
    var userListLastPosition: Int = 0


    val postsChanged : MutableLiveData<List<Int>> by lazy {
        MutableLiveData<List<Int>>()
    }

    val userChanged : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val userClick : MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    val postClick : MutableLiveData<Post> by lazy {
        MutableLiveData<Post>()
    }

    val onSearch : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }


    var posts = mutableListOf<Post>()
    var users = mutableListOf<User>()

    var allAppUsers = mutableListOf<User>()

    init {
        if(app.currentUser != null){
            subscribeChanges()
        }
    }

    fun setup(){
        currentUser = RealmRepository.getMyUser()
        allAppUsers = RealmRepository.getAllUsers().toMutableList()
    }

    fun search(keyword : String){
        searchText = keyword

        if (searchText.isNotEmpty()) {
            posts = RealmRepository.getPostsByKeyword(searchText).toMutableList()
            users = RealmRepository.getUsersByKeyword(searchText).toMutableList()
        }else{
            posts = mutableListOf()
            users = mutableListOf()
        }

        userListLastPosition = 0
        postListLastPositon = 0

        onSearch.postValue(true)
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
                if (changes is UpdatedResults && changes.changes.isNotEmpty()) {
                    for (i in changes.changes) {
                        val updatedPost = changes.list[i]
                        val indexToUpdate = posts.indexOfFirst { it._id == updatedPost._id }
                        if (indexToUpdate != -1) {
                            posts[indexToUpdate] = updatedPost
                            postsChanged.postValue(listOf(indexToUpdate))
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
                            userChanged.postValue(indexToUpdate)
                        }

                        val indexToUpdateAllUser = allAppUsers.indexOfFirst { it._id == updatedUser._id }
                        if (indexToUpdateAllUser != -1) {
                            allAppUsers[indexToUpdateAllUser] = updatedUser
                        }

                        val postIndexesToUpdate = posts.mapIndexedNotNull { index, post ->
                            if (post.owner_id == updatedUser.owner_id) {
                                index
                            } else {
                                null
                            }
                        }

                        if (postIndexesToUpdate.isNotEmpty()) {
                            postsChanged.postValue(postIndexesToUpdate)
                        }
                    }
                }
            }
        }
    }

    fun postListScrollTo(indexItemRV: Int, topViewRV: Int) {
        postListLastPositon = indexItemRV
        postListOffset = topViewRV
    }

}