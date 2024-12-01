package com.dicoding.learn.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.learn.data.model.UserModel
import com.dicoding.learn.data.model.UserPreference
import com.dicoding.learn.data.model.LoginResponse
import com.dicoding.learn.data.model.RegisterResponse
import com.dicoding.learn.data.remote.ApiService
import com.dicoding.learn.helpers.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class AuthRepository(
    private val apiService : ApiService,
    private val userPreference: UserPreference
) {

    fun registerUser(name : String, email : String, password : String) : LiveData<Result<RegisterResponse>> = liveData {
        try {
            emit(Result.Loading)
            val res = apiService.register(name, email, password)

            if(!res.error!!) {
                emit(Result.Success(res))
            } else {
                emit(Result.Error(res.message ?: "Unknown Error"))
            }
        } catch (e : HttpException) {
           try {
               val errorRes = e.response()?.errorBody()?.string()
               val gson = Gson()
               val parseError = gson.fromJson(errorRes, RegisterResponse::class.java)
               emit(Result.Success(parseError))
           } catch (exception : Exception) {
               emit(Result.Error("Error parsing exception response"))
           }
        }
    }

    fun loginUser(email: String, password: String) : LiveData<Result<LoginResponse>> = liveData {
        try {
            emit(Result.Loading)
            val res = apiService.login(email, password)

            if (!res.error!!) {
                emit(Result.Success(res))
            } else {
                emit(Result.Error(res.message ?: "Unknown Error"))
            }

        } catch (e : HttpException) {
            try {
                val errorRes = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parseError = gson.fromJson(errorRes, LoginResponse::class.java)
                emit(Result.Success(parseError))
            } catch (exception : Exception) {
                emit(Result.Error("Error parsing exception response"))
            }
        }
    }

    suspend fun saveSession(user : UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession() : Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout(){
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var INSTANCE : AuthRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ) : AuthRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthRepository(apiService, userPreference)
            }
    }
}