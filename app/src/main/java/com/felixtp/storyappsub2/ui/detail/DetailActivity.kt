package com.felixtp.storyappsub2.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.felixtp.storyappsub2.R
import com.felixtp.storyappsub2.databinding.ActivityDetailBinding
import com.felixtp.storyappsub2.ui.ViewModelFactory
import com.felixtp.storyappsub2.ui.utils.withDateFormat

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        observeToken()
        observeIndicator()
    }

    private fun observeToken() {
        val idUser = intent.getStringExtra(STORY_ID)
        if (idUser != null) {
            viewModel.getToken().observe(this) { token ->
                observeStoryDetail(token, idUser)
            }
        }
    }

    private fun observeStoryDetail(token: String, id: String) {
        viewModel.getDetailStory(token, id).observe(this) { detailStory ->
            Glide.with(this)
                .load(detailStory.photoUrl)
                .into(binding.imgDetailPhoto)

            binding.apply {
                tvDetailName.text = detailStory.name
                tvDetailTime.text = getString(
                    R.string.dateFormat,
                    detailStory.createdAt.withDateFormat()
                )
                tvDetailDescription.text = getString(
                    R.string.description_with,
                    detailStory.description
                )
            }
        }
    }

    private fun observeIndicator() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isError.observe(this) { isError ->
            if (isError) {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val STORY_ID = "id"
    }
}
