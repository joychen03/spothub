package com.itb.dam.jiafuchen.spothub.ui.viemodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class AddPostViewModel @Inject constructor(): ViewModel() {

    var title : String = ""
    var description : String = ""
    var image : Uri? = null
    var location : LatLng = LatLng(0.0, 0.0)

    fun setup() {
        println("caca")
    }

}