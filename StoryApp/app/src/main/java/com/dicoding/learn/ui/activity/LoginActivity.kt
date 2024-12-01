package com.dicoding.learn.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.learn.R
import com.dicoding.learn.data.model.UserModel
import com.dicoding.learn.databinding.ActivityLoginBinding
import com.dicoding.learn.helpers.Result
import com.dicoding.learn.helpers.showMaterialDialog
import com.dicoding.learn.ui.viewmodels.AuthenticationViewModel
import com.dicoding.learn.ui.viewmodels.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private val viewModel by viewModels<AuthenticationViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.login(email, password).observe(this) {user ->
                when(user) {
                    is Result.Loading -> {
                        binding.loginLoading.visibility = View.VISIBLE
                    }
                    is Result.Error -> {
                        binding.loginLoading.visibility = View.GONE
                        showMaterialDialog(
                            context = this@LoginActivity,
                            title = "Login Failed",
                            message = user.error,
                            positiveButtonText = "Retry"
                        )
                    }
                    is Result.Success -> {
                        binding.loginLoading.visibility = View.GONE
                        if(user.data.error == true) {
                            showMaterialDialog(this@LoginActivity, "Login Failed", user.data.message!!, "Retry")
                        } else {
                            user.data.loginResult?.let {
                                showMaterialDialog(this@LoginActivity, "Login Success", getString(R.string.login_success), "Ok")
                                viewModel.saveSessionData(UserModel(email, it.token!!, true))
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }
        }
        binding.gotoRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, 30f, -30f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val pageTitle = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(RegisterActivity.APPEAR_ANIMATION_DURATION)
        val emailTv = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(RegisterActivity.APPEAR_ANIMATION_DURATION)
        val passwordTv = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(RegisterActivity.APPEAR_ANIMATION_DURATION)
        val emailEt = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(RegisterActivity.APPEAR_ANIMATION_DURATION)
        val passwordEt = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(RegisterActivity.APPEAR_ANIMATION_DURATION)
        val btnLogin = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(RegisterActivity.APPEAR_ANIMATION_DURATION)


        AnimatorSet().apply {
            playSequentially(
                pageTitle,
                emailTv,
                emailEt,
                passwordTv,
                passwordEt,
                btnLogin
            )
            startDelay = RegisterActivity.APPEAR_ANIMATION_DURATION
        }.start()
    }

}