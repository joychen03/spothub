package com.itb.dam.jiafuchen.spothub.ui.viemodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostEditViewModel @Inject constructor() : ViewModel() {

    var title : String? = null
    var description : String? = null
    var image : Uri? = null
    var location : LatLng? = null

    //TODO

    fun clearAll() {
        this.title = null
        this.description = null
        this.image = null
        this.location = null
    }

}