package com.bangkit.capstone.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailResponse(

    val id: String,
    val username: String,
    val avatarUrl: String,
    val descriptions: String,

): Parcelable