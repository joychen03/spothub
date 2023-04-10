package com.itb.dam.jiafuchen.spothub.ui.viemodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(

) : ViewModel() {
    private val _searchText = MutableLiveData<String>()
    val searchText : LiveData<String> = _searchText

    init {
        //get last location

    }

    fun searchMap(text: String){
        _searchText.postValue(text)
    }


}