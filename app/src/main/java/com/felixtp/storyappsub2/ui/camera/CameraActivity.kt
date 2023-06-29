package com.felixtp.storyappsub2.ui.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.felixtp.storyappsub2.R
import com.felixtp.storyappsub2.databinding.ActivityCameraBinding
import com.felixtp.storyappsub2.ui.main.MainActivity
import java.io.File

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var imageCapture: ImageCapture
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private val requiredPermissions = arrayOf(Manifest.permission.CAMERA)
    private val requestCodePermissions = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                requestCodePermissions
            )
        }

        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    private fun setupClickListeners() {
        binding.apply {
            switchCamera.setOnClickListener {
                cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
                startCamera()
            }

            captureImage.setOnClickListener {
                takePicture()
            }

            openGallery.setOnClickListener {
                openGallery()
            }

            btnBack.setOnClickListener {
                startActivity(Intent(this@CameraActivity, MainActivity::class.java))
            }
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }
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

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                showToast(getString(R.string.failed_open_camera))
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePicture() {
        val photoFile = createFile(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    showToast(getString(R.string.success_take_picture))
                    intentFile(photoFile)
                }

                override fun onError(exception: ImageCaptureException) {
                    showToast(getString(R.string.failed_take_picture))
                }
            }
        )
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@CameraActivity)
                intentFile(myFile, true)
            }
        }
    }

    private fun intentFile(file: File, isGallery: Boolean = false) {
        val intent = Intent(this@CameraActivity, AddStoryActivity::class.java).apply {
            putExtra(AddStoryActivity.PICTURE, file)
            putExtra(
                AddStoryActivity.IS_BACK_CAMERA,
                cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
            )
            putExtra(AddStoryActivity.IS_GALLERY, isGallery)
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this@CameraActivity, message, Toast.LENGTH_SHORT).show()
    }
}
