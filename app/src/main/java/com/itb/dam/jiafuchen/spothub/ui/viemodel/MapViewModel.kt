package com.itb.dam.jiafuchen.spothub.ui.viemodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.internal.ListenerHolder.ListenerKey
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
class MapViewModel @Inject constructor(

) : ViewModel() {
    private val _searchText = MutableLiveData<String>()
    val searchText : LiveData<String> = _searchText

    var currentUser : User? = null
    var postList = mutableListOf<Post>()

    val postUpdated : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val postAdded : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val postDeleted : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    init {
        subscribeChanges()
    }

    fun setup(){
        currentUser = RealmRepository.getMyUser()
        postList = RealmRepository.getMyPosts(app.currentUser!!.id) .toMutableList().asReversed()
    }

    fun searchMap(text: String){
        _searchText.postValue(text)
    }

    private fun subscribeChanges(){
        viewModelScope.launch {
            RealmRepository.getMyPostsAsFlow().collect { changes ->
                if(changes is UpdatedResults){
                    for (i in changes.insertions) {
                        val addedPost = changes.list[i]
                        postList.add(0, addedPost)
                        postAdded.postValue(true)
                    }

                    for (i in changes.changes) {
                        val updatedPost = changes.list[i]
                        val indexToUpdate = postList.indexOfFirst { it._id == updatedPost._id }
                        if (indexToUpdate != -1) {
                            postList[indexToUpdate] = updatedPost
                            postUpdated.postValue(indexToUpdate)
                        }
                    }

                    for (i in changes.deletions) {
                        postList = RealmRepository.getMyPosts(app.currentUser!!.id).toMutableList().asReversed()
                        postDeleted.postValue(true)
                    }
                }
            }
        }
    }


}