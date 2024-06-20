package com.bangkit.capstone.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.capstone.response.Feed
import com.bangkit.capstone.response.RandomContent
import com.bangkit.capstone.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val loading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = loading

    private val feeds = MutableLiveData<List<Feed>>()
    val feedsLiveData: LiveData<List<Feed>> = feeds

    fun getData(token: String) {
        loading.value = true
        val call = ApiConfig.getApiService(token).getData()
        call.enqueue(object : Callback<RandomContent> {
            override fun onResponse(
                call: Call<RandomContent>,
                response: Response<RandomContent>
            ) {
                loading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {

                        if (!responseBody.feeds.isNullOrEmpty()) {
                            feeds.value = responseBody.feeds
                        } else {
                            Log.e(TAG, "onResponse: Feeds is null or empty")
                            feeds.value = emptyList()
                        }
                    } else {
                        Log.e(TAG, "onResponse: Response body is null")
                    }
                } else {
                    Log.e(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RandomContent>, t: Throwable) {
                loading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
