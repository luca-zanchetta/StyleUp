package com.example.styleup

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.content.Intent
import android.view.TextureView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.Manifest
import android.icu.text.SimpleDateFormat
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import java.io.File
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : AppCompatActivity() {

    private lateinit var textureView: TextureView
    private lateinit var captureButton: Button
    private lateinit var imageCapture: ImageCapture
    private val CAMERA_PERMISSION_REQUEST = 1001
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_fragment)
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
        
        val intent = Intent(this, ConfirmPhotoActivity::class.java)

        captureButton = findViewById(R.id.captureButton)

        val cameraPermission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(this, cameraPermission) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(cameraPermission), CAMERA_PERMISSION_REQUEST)
        }
    }
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
    private fun openCamera() {
        val textureView: PreviewView = findViewById(R.id.previewView)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(textureView.surfaceProvider) }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Add ImageCapture use case
                imageCapture = ImageCapture.Builder()
                    .setTargetRotation(textureView.display.rotation)
                    .build()

                captureButton.setOnClickListener {
                    Log.d("CameraFragment", "CLICK")

                    val photoFile = File(
                        outputDirectory,
                        SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg"
                    )

                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    imageCapture.takePicture(
                        outputOptions,
                        cameraExecutor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onError(error: ImageCaptureException) {
                                Log.e("Capture Image", "Error capturing image: ${error.message}", error)
                            }

                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                Log.d("Capture Image", "Image captured successfully: ${output.savedUri}")
                            }
                        }
                    )
                }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("openCamera()", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }
}