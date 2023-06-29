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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.felixtp.storyappsub2.R
import com.felixtp.storyappsub2.databinding.ActivityRegisterBinding
import com.felixtp.storyappsub2.ui.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setupViews()
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
        setupRegisterButton()
    }

    private fun setupRegisterButton() {
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (binding.edRegisterEmail.error == null && binding.edRegisterPassword.error == null) {
                    registerUser(name, email, password)
                }
            } else {
                showToast(getString(R.string.fill_out_all_the_field))
            }
        }

        observeIndicator()
    }

    private fun registerUser(name: String, email: String, password: String) {
        viewModel.register(name, email, password)
    }

    private fun observeIndicator() {
        viewModel.apply {
            isLoading.observe(this@RegisterActivity) { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }

            isSuccessRegister.observe(this@RegisterActivity) { isSuccess ->
                if (isSuccess) {
                    navigateToLoginActivity()
                }
            }

            isError.observe(this@RegisterActivity) { isError ->
                if (isError) {
                    showToast(getString(R.string.error))
                }
            }

            message.observe(this@RegisterActivity) { message ->
                Toast.makeText(this@RegisterActivity, message.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLoginActivity() {
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        finishAffinity()
    }

    private fun playAnimation() {
        val edName = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val edEmail = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val edPassword = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(edName, edEmail, edPassword, btnRegister)
        }.start()
    }
}
