package com.bangkit.capstone.response

import com.google.gson.annotations.SerializedName

data class AddFeeds(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("message")
    val message: String
)