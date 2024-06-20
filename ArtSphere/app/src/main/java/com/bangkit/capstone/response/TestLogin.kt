package com.bangkit.capstone.response

data class TestLogin(
    val accessToken: String,
    val email: String,
    val id: String,
    val message: String,
    val username: String
)