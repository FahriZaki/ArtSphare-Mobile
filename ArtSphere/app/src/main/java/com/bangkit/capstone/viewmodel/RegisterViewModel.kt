package com.bangkit.capstone.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.capstone.response.RegisterRequest
import com.bangkit.capstone.response.TestRegister
import com.bangkit.capstone.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private var _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private var _isLoading = MutableLiveData<Boolean>()

    private var _errorMessage = MutableLiveData<String>()

    fun registerUser(name: String, email: String, password: String) {
        if (!isEmailValid(email)) {
            _errorMessage.value = "Invalid email format"
            return
        }

        val regRequest = RegisterRequest(name, email, password)
        _isLoading.value = true
        val call = ApiConfig.getApiService("").register(regRequest)
        call.enqueue(object : Callback<TestRegister> {
            override fun onResponse(
                call: Call<TestRegister>,
                response: Response<TestRegister>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.userId.isNotEmpty()) {
                        _isSuccess.value = true
                        _errorMessage.value = responseBody.message
                        Log.d(TAG, "onResponse: ${responseBody.message}")
                    } else {
                        _isSuccess.value = false
                        _errorMessage.value = "Register failed, empty username"
                        Log.e(TAG, "onResponse: Register failed, empty username")
                    }
                } else {
                    _isSuccess.value = false
                    val errorMessage = response.errorBody()?.string() ?: "Register failed"
                    _errorMessage.value = errorMessage
                    Log.e(TAG, "onResponse: $errorMessage")
                }
            }

            override fun onFailure(call: Call<TestRegister>, t: Throwable) {
                _isLoading.value = false
                _isSuccess.value = false
                _errorMessage.value = t.message ?: "Unknown error"
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun isEmailValid(email: String): Boolean {
        val emailPattern1 = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return email.matches(emailPattern1)
    }

    companion object {
        private const val TAG = "RegisterViewModel"
    }
}