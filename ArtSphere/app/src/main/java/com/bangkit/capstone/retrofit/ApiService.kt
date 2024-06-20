package com.bangkit.capstone.retrofit

import com.bangkit.capstone.response.AddFeeds
import com.bangkit.capstone.response.RandomContent
import com.bangkit.capstone.response.TestLogin
import com.bangkit.capstone.response.RegisterRequest
import com.bangkit.capstone.response.TestRegister
import com.bangkit.capstone.response.ScanResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("register")
    fun register(@Body regCredential: RegisterRequest): Call<TestRegister>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("emailOrUsername") emailOrUsername: String,
        @Field("password") password: String
    ): Call<TestLogin>

    @GET("get-feeds")
    fun getData(
    ): Call<RandomContent>

    @Multipart
    @POST("predict")
    fun predictImage(
        @Part file: MultipartBody.Part
    ): Call<ScanResponse>


    @Multipart
    @POST("add-feeds")
    fun upload(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<AddFeeds>

}
