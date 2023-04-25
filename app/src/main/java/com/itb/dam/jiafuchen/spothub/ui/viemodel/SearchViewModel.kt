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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(): ViewModel() {
    var searchText : String = ""
    var currentUser : User? = null
    var postRvScrollOffset: Int = 0
    var postRvScrollPosition: Int = 0
    var userRvScrollOffset: Int = 0
    var userRvScrollPosition: Int = 0

    val postsChanged : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val usersChanged : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val allUserChanged : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val userClick : MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    val postClick : MutableLiveData<Post> by lazy {
        MutableLiveData<Post>()
    }

    val onSearch : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }


    var posts = mutableListOf<Post>()
    var users = mutableListOf<User>()

    var allAppUsers = mutableListOf<User>()

    fun setup(){
        if(app.currentUser == null){
            return
        }

        currentUser = RealmRepository.getMyUser()
        allAppUsers = RealmRepository.getAllUsers().toMutableList()

        viewModelScope.launch {
            RealmRepository.getPostsAsFlow().collect { changes: ResultsChange<Post> ->
                if (changes is UpdatedResults && changes.changes.isNotEmpty()) {
                    for (i in changes.changes) {
                        val updatedPost = changes.list[i]
                        val indexToUpdate = posts.indexOfFirst { it._id == updatedPost._id }
                        if (indexToUpdate != -1) {
                            posts[indexToUpdate] = updatedPost
                            postsChanged.postValue(indexToUpdate)
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            var count = 0
            RealmRepository.getAllUsersAsFlow().collect { changes: ResultsChange<User> ->
                println("IM CHANGED")
                if(changes is UpdatedResults && changes.changes.isNotEmpty() && changes.changeRanges.isEmpty()){
                    for(i in changes.changes) {
                        val updatedUser = changes.list[i]
                        val indexToUpdate = users.indexOfFirst { it._id == updatedUser._id }
                        if(indexToUpdate != -1){
                            users[indexToUpdate] = updatedUser
                            usersChanged.postValue(indexToUpdate)
                        }
                    }
                }
            }
        }
    }

    fun search(keyword : String){
        if(app.currentUser != null){
            posts = RealmRepository.getPostsByKeyword(searchText).toMutableList()
            users = RealmRepository.getUsersByKeyword(searchText).toMutableList()

            postRvScrollOffset = 0
            postRvScrollPosition = 0

            userRvScrollOffset = 0
            userRvScrollPosition = 0

            onSearch.postValue(keyword)
        }
    }

    fun getAllUsers(): List<User> {
        return RealmRepository.getAllUsers()
    }
    fun postListScrollTo(indexItemRV: Int, topViewRV: Int) {
        postRvScrollPosition = indexItemRV
        postRvScrollOffset = topViewRV
    }
    fun userListScrollTo(indexItemRV: Int, topViewRV: Int) {
        userRvScrollPosition = indexItemRV
        userRvScrollOffset = topViewRV
    }

    fun goToUser(user: User) {
        userClick.postValue(user)
    }

    fun goToPost(post: Post) {
        postClick.postValue(post)
    }


}