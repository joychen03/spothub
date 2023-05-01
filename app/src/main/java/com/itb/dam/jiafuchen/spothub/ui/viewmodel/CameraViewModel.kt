package com.itb.dam.jiafuchen.spothub.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private val _tookPhoto = MutableLiveData<Uri?>()
    val tookPhoto : LiveData<Uri?> = _tookPhoto


    fun takePhoto(uri : Uri){
        _tookPhoto.postValue(uri)
    }

    fun reset(){
        _tookPhoto.postValue(null)
    }
}