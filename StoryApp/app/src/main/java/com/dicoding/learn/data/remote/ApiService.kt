package com.dicoding.learn.data.remote

import com.dicoding.learn.data.model.AddStoryResponse
import com.dicoding.learn.data.model.LoginResponse
import com.dicoding.learn.data.model.RegisterResponse
import com.dicoding.learn.data.model.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page : Int = 1,
        @Query("size") size : Int = 20
    ): StoriesResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat : RequestBody? = null,
        @Part("lon") lon : RequestBody? = null
    ): AddStoryResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location : Int = 1,
    ) : StoriesResponse
}