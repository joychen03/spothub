package com.itb.dam.jiafuchen.spothub.domain.model
import android.net.Uri
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
class AddEditPostArgs(val image: Uri? = null, val location : LatLng? = null, val post : Post? = null): Parcelable