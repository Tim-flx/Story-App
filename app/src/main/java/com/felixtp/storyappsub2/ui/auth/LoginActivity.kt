package com.felixtp.storyappsub2.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.felixtp.storyappsub2.R
import com.felixtp.storyappsub2.data.remote.response.LoginResponse
import com.felixtp.storyappsub2.databinding.ActivityLoginBinding
import com.felixtp.storyappsub2.ui.ViewModelFactory
import com.felixtp.storyappsub2.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setupViews()
        setupBackButton()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    private fun hideSystemUI() {
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

    private fun setupViews() {
        playAnimation()
        notHaveAccount()
        setupLoginButton()
    }

    private fun setupBackButton() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToWelcomeScreen()
            }
        })
    }

    private fun navigateToWelcomeScreen() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finishAffinity()
    }

    private fun notHaveAccount() {
        binding.tvLoginCrateAcc.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupLoginButton() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (binding.edLoginEmail.error == null && binding.edLoginPassword.error == null) {
                    loginUser(email, password)
                }
            } else {
                showToast(getString(R.string.fill_out_all_the_field))
            }
        }

        observeLoginResponse()
        observeIndicator()
    }

    private fun loginUser(email: String, password: String) {
        viewModel.login(email, password)
    }

    private fun observeLoginResponse() {
        viewModel.apply {
            isError.observe(this@LoginActivity) { isError ->
                if (isError) {
                    showToast(getString(R.string.error))
                }
            }

            loginResponse.observe(this@LoginActivity) { loginResponse ->
                showToast(loginResponse.message)
            }
        }
    }

    private fun savePreference(data: LoginResponse) {
        viewModel.saveUser(data.loginResult.token, isLogin = true)
    }

    private fun observeIndicator() {
        viewModel.apply {
            isLoading.observe(this@LoginActivity) { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }

            isSuccessLogin.observe(this@LoginActivity) { isSuccess ->
                if (isSuccess) {
                    loginResponse.observe(this@LoginActivity) { loadingResponse ->
                        savePreference(loadingResponse)
                    }

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finishAffinity()
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun playAnimation() {
        val edEmail = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val edPassword = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(500)
        val tvCreateAcc = ObjectAnimator.ofFloat(binding.tvLoginCrateAcc, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(edEmail, edPassword, btnLogin, tvCreateAcc)
        }.start()
    }
}
