package com.bangkit.capstone.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.capstone.response.TestLogin
import com.bangkit.capstone.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> = _token

    fun loginUser(emailOrUsername: String, password: String) {
        _isLoading.value = true
        val call = ApiConfig.getApiService("").login(emailOrUsername, password)
        call.enqueue(object : Callback<TestLogin> {
            override fun onResponse(call: Call<TestLogin>, response: Response<TestLogin>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d(TAG, "Response Body: $responseBody")
                    val accessToken = responseBody?.accessToken
                    Log.d(TAG, "AccessToken: $accessToken")
                    if (accessToken != null) {
                        _isSuccess.value = true
                        _token.value = accessToken
                    } else {
                        Log.e(TAG, "AccessToken null")
                        _isSuccess.value = false
                    }
                } else {
                    Log.e(TAG, "Login failed")
                    _isSuccess.value = false
                }
            }

            override fun onFailure(call: Call<TestLogin>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                _isSuccess.value = false
            }
        })
    }

    fun isEmailValid(email: String): Boolean {
        val emailPattern = Regex("[a-zA-Z0-9._-]")
        return email.matches(emailPattern)
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}
