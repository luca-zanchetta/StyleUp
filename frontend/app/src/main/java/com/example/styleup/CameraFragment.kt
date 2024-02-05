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
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Paint
import android.icu.text.SimpleDateFormat
import android.media.Image
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import java.io.File
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.net.Uri
import android.view.Surface
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.core.net.toUri
import com.example.styleup.ml.AutoModel4
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

class CameraFragment : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var captureButton: Button
    private lateinit var imageCapture: ImageCapture
    private val CAMERA_PERMISSION_REQUEST = 1001
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    lateinit var bitmap: Bitmap
    lateinit var model: AutoModel4
    lateinit var imageProcessor: ImageProcessor
    lateinit var imageView: ImageView
    private lateinit var imageAnalyzer: ImageAnalysis
    val paint = Paint()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_fragment)
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        imageProcessor = ImageProcessor.Builder().add(ResizeOp(192, 192, ResizeOp.ResizeMethod.BILINEAR)).build()
        model = AutoModel4.newInstance(this)
        captureButton = findViewById(R.id.captureButton)
        imageView = findViewById(R.id.photoImageView)
        previewView = findViewById(R.id.previewView)
        paint.setColor(Color.YELLOW)

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }



        val cameraPermission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(this, cameraPermission) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(cameraPermission), CAMERA_PERMISSION_REQUEST)
        }
    }

    @OptIn(ExperimentalGetImage::class) private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .setTargetRotation(previewView.display.rotation)
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetRotation(previewView.display.rotation)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        // Process the image and update UI here
                        if (imageProxy.image != null) {
                            processAndDrawImage(imageProxy.image!!)
                        }
                        else {
                            Log.e("CameraFragment", "imageProxy.image is null!")
                        }
                        imageProxy.close()
                    }
                }

            preview.setSurfaceProvider(previewView.surfaceProvider)

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Add ImageCapture use case
                imageCapture = ImageCapture.Builder()
                    .setTargetRotation(previewView.display.rotation)
                    .build()

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, imageAnalyzer)

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
                                val editor = sharedPreferences.edit()
                                editor.putString("savedUri", output.savedUri.toString())
                                editor.apply()

                                val intent = Intent(this@CameraFragment, ConfirmPhotoActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("openCamera()", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        model.close()
    }

    private fun processAndDrawImage(image: Image) {
        val yuvToRgbConverter= YuvToRgbConverter(this)
        bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        yuvToRgbConverter.yuvToRgb(image, bitmap)

        val rotationDegrees = when (previewView.display.rotation) {
            Surface.ROTATION_0 -> 90
            else -> 0
        }
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        var tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(rotatedBitmap)
        tensorImage = imageProcessor.process(tensorImage)

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 192, 192, 3), DataType.UINT8)
        inputFeature0.loadBuffer(tensorImage.buffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        // val outputFeature0 = outputs.outputFeature0AsTensorBuffer contains the predictions!
        /*
        0 nose
        1 leftEye
        2 rightEye
        3 leftEar
        4 rightEar
        5 leftShoulder
        6 rightShoulder
        7 leftElbow
        8 rightElbow
        9 leftWrist
        10 rightWrist
        11 leftHip
        12 rightHip
        13 leftKnee
        14 rightKnee
        15 leftAnkle
        16 rightAnkle

        For each keypoint, there are three coordinates: x, y, confidence
         */

        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

        var mutable = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true)
        var canvas = Canvas(mutable)

        var h = rotatedBitmap.height
        var w = rotatedBitmap.width
        var x = 0

        while(x <= 49) {
            if(outputFeature0.get(x+2) > 0.45) {
                // Draw circle on scaled coordinates
                canvas.drawCircle(outputFeature0.get(x+1)*w, outputFeature0.get(x)*h, 10f, paint)
            }
            x += 3  // Go to the next point
        }

        runOnUiThread {
            imageView.setImageBitmap(mutable)
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
}