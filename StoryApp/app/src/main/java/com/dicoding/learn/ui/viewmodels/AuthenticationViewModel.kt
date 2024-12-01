package com.dicoding.learn.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.learn.data.model.UserModel
import com.dicoding.learn.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun login(email : String, password : String) = authRepository.loginUser(email, password)

    fun register(name : String, email: String, password: String) = authRepository.registerUser(name, email, password)

    fun saveSessionData(userData : UserModel) = viewModelScope.launch {
        authRepository.saveSession(userData)
    }
}