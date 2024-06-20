package com.bangkit.capstone.response


data class TestRegister(

    val message: String,

    val userId: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)