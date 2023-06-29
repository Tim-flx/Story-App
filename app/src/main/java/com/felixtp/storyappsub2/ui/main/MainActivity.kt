package com.felixtp.storyappsub2.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.felixtp.storyappsub2.R
import com.felixtp.storyappsub2.databinding.ActivityMainBinding
import com.felixtp.storyappsub2.ui.ViewModelFactory
import com.felixtp.storyappsub2.ui.auth.WelcomeActivity
import com.felixtp.storyappsub2.ui.camera.CameraActivity
import com.felixtp.storyappsub2.ui.maps.MapsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setupViews()
        setupOnClickListeners()
        setupOnBackPressed()

        observeToken()
    }

    private fun setupViews() {
        storyAdapter = StoryAdapter(this)
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
        }
    }

    private fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

    private fun setupOnClickListeners() {
        binding.fabLogout.setOnClickListener {
            viewModel.setLogout()
            startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
            finishAffinity()
        }
        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this@MainActivity, CameraActivity::class.java))
        }
        binding.fabSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        binding.fabMaps.setOnClickListener {
            startActivity(Intent(this@MainActivity, MapsActivity::class.java))
        }
    }

    private fun observeStoryList(token: String) {
        viewModel.getAllStories(token).observe(this@MainActivity) { stories ->
            storyAdapter.submitData(lifecycle, stories)
            binding.rvStories.scrollToPosition(0)
        }
    }

    private fun observeToken() {
        viewModel.getToken().observe(this@MainActivity) { token ->
            if (token.isNotEmpty()) {
                observeStoryList(token)
                observeIndicator()
            }
        }
    }

    private fun observeIndicator() {
        viewModel.apply {
            isLoading.observe(this@MainActivity) { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }

            isError.observe(this@MainActivity) { isError ->
                if (isError) {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
