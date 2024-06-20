package com.bangkit.capstone.response

import android.os.Parcelable

import kotlinx.parcelize.Parcelize

@Parcelize
data class Feed(
    val createdAt: String,
    val description: String,
    val id: String,
    val mediaUrl: String
) : Parcelable