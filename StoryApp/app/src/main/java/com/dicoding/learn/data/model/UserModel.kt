package com.dicoding.learn.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val email : String,
    val token : String,
    val isLoading : Boolean = false
) : Parcelable
