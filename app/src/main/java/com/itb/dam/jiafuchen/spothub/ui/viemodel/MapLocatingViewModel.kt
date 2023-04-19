package com.itb.dam.jiafuchen.spothub.ui.viemodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapLocatingViewModel @Inject constructor() : ViewModel() {

    private val _lastMarker = MutableLiveData<MarkerOptions?>()
    val lastMarker : LiveData<MarkerOptions?> = _lastMarker

    fun setMarker(marker : MarkerOptions?){
        _lastMarker.postValue(marker)
    }
}