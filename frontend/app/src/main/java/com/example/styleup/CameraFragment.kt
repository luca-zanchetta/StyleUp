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
import android.graphics.RectF
import android.icu.text.SimpleDateFormat
import android.media.Image
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import java.io.File
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.net.Uri
import android.util.Base64
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.nio.ByteBuffer

data class GetShirtByIdResponse(val shirt: String?, val status: Int)
interface GetShirtByIdAPI {
    @GET("getShirtById")
    fun getShirtById(@Query("id") shirtId: Int): Call<GetShirtByIdResponse>
}

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
    var shirtBitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    var shirtBitmapCopy = shirtBitmap.copy(Bitmap.Config.ARGB_8888, true)
    lateinit var cameraProvider: ProcessCameraProvider
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


        // Retrieve selected shirt
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val shirtId = sharedPreferences.getInt("shirtId", 0)

        val getShirtByIdApiService = retrofit.create(GetShirtByIdAPI::class.java)
        getShirtByIdApiService.getShirtById(shirtId).enqueue(object : Callback<GetShirtByIdResponse> {
            override fun onResponse(call: Call<GetShirtByIdResponse>, response: Response<GetShirtByIdResponse>) {
                Log.d("CameraFragment", "GETSHIRTBYIDOK")
                try {
                    // Access the result using response.body()
                    val result: GetShirtByIdResponse? = response.body()

                    // Check if the result is not null before accessing properties
                    result?.let {
                        val status = it.status
                        if (status == 200) {
                            Log.d("CameraFragment", "ShirtOK")
                            val shirtByteArray = Base64.decode(it.shirt, Base64.DEFAULT)
                            shirtBitmapCopy = BitmapFactory.decodeByteArray(shirtByteArray, 0, shirtByteArray!!.size)
                        }
                        else {
                            Log.e("CameraFragment", "${it.status}")
                        }
                    }
                } catch (e: Exception) {
                    // Do nothing
                    Log.e("CameraFragment", "[ERROR] "+e.toString())
                }
            }
            override fun onFailure(call: Call<GetShirtByIdResponse>, t: Throwable) {
                Log.e("CameraFragment", "[ERR] ${t.message}")
                // retry here
            }
        })

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

    override fun onDestroy() {
        super.onDestroy()
        cameraProvider?.unbindAll() // where cameraProvider is a reference to the ProcessCameraProvider
        cameraExecutor.shutdown()
        model.close()
    }

    @OptIn(ExperimentalGetImage::class) private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .setTargetRotation(previewView.display.rotation)
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetRotation(previewView.display.rotation)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        try {
                            // Process the image and update UI here
                            if (imageProxy.image != null) {
                                processAndDrawImage(imageProxy.image!!)
                            }
                            else {
                                Log.e("CameraFragment", "imageProxy.image is null!")
                            }
                        } finally {
                            imageProxy.close()
                        }
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

        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

        var mutable = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true)
        var canvas = Canvas(mutable)

        var h = rotatedBitmap.height
        var w = rotatedBitmap.width
        var x = 0

        /*while(x <= 49) {
            if(outputFeature0.get(x+2) > 0.45) {
                // Draw circle on scaled coordinates
                canvas.drawCircle(outputFeature0.get(x+1)*w, outputFeature0.get(x)*h, 10f, paint)
            }
            x += 3  // Go to the next point
        }*/

        // Overlay the shirt on the image
        overlayShirt(mutable, outputFeature0)
    }

    private fun overlayShirt(image: Bitmap, outputFeature0: FloatArray) {
        val canvas = Canvas(image)
        val paint2 = Paint(Paint.ANTI_ALIAS_FLAG)

        val h = image.height.toFloat()
        val w = image.width.toFloat()

        /*
        0 nose              y = 0; x = 1; conf = 2
        1 leftEye           y = 3; x = 4; conf = 5
        2 rightEye          y = 6; x = 7; conf = 8
        3 leftEar           y = 9; x = 10; conf = 11
        4 rightEar          y = 12; x = 13; conf = 14
        5 leftShoulder      y = 15; x = 16; conf = 17
        6 rightShoulder     y = 18; x = 19; conf = 20
        7 leftElbow         y = 21; x = 22; conf = 23
        8 rightElbow        y = 24; x = 25; conf = 26
        9 leftWrist         y = 27; x = 28; conf = 29
        10 rightWrist       y = 30; x = 31; conf = 32
        11 leftHip          y = 33; x = 34; conf = 35
        12 rightHip         y = 36; x = 37; conf = 38
        13 leftKnee         y = 39; x = 40; conf = 41
        14 rightKnee        y = 42; x = 43; conf = 44
        15 leftAnkle        y = 45; x = 46; conf = 47
        16 rightAnkle       y = 48; x = 49; conf = 50

        For each keypoint, there are three coordinates: x, y, confidence
         */

        // Get the corner points
        val topLeftX = outputFeature0[16] * w
        val topLeftY = outputFeature0[15] * h
        val topRightX = outputFeature0[19] * w
        val topRightY = outputFeature0[18] * h
        val bottomLeftX = outputFeature0[34] * w
        val bottomLeftY = outputFeature0[33] * h
        val bottomRightX = outputFeature0[37] * w
        val bottomRightY = outputFeature0[36] * h

        // Calculate the position to overlay the shirt
        val left = topLeftX
        val top = topLeftY
        val right = topRightX
        val bottom = bottomRightY


        // Draw the shirt on the canvas
        canvas.drawBitmap(shirtBitmapCopy, null, RectF(left, top, right, bottom), paint2)

        runOnUiThread {
            imageView.setImageBitmap(image)
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