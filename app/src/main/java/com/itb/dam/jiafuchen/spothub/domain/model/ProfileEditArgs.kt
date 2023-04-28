package com.itb.dam.jiafuchen.spothub.domain.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileEditArgs(val image: Uri? = null): Parcelable