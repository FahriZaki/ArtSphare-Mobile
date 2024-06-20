package com.bangkit.capstone.response

import com.google.gson.annotations.SerializedName

data class RandomContent(

    @field:SerializedName("listContent")
    val listContent: List<ListContent>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("feeds")
    val feeds: List<Feed>,

    @field:SerializedName("message")
    val message: String
)

data class ListContent(

    @field:SerializedName("media")
    val mediaUrl: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("description")
    val description: String? = null,


    @field:SerializedName("id")
    val id: String? = null,

    )