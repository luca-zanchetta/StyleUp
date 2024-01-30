package com.example.styleup

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.File
import java.util.Date
import java.util.Locale
import com.google.gson.annotations.SerializedName

data class ShirtBackend(val id: Int, val shirt: String, val shirtName: String)
data class GetShirtsResponse(val shirts: List<ShirtBackend>, val status: Int)
interface GetShirtsAPI {
    @GET("getShirts")
    fun getShirts(): Call<GetShirtsResponse>
}

val apiGetShirts = retrofit.create(GetShirtsAPI::class.java)

class ShirtsFragment: Fragment(), ShirtsAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var shirtsAdapter: ShirtsAdapter

    private val takePictureLauncher = //contratto di attività
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                // La foto è stata scattata con successo
                // Puoi gestire la foto qui
            } else {
                // La foto non è stata scattata o è stata annullata
            }
        }
    private val requestCameraPermission = //autorizzazione per la fotocamera
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Il permesso è stato concesso, avvia la fotocamera
                takePictureLauncher.launch(null)
            } else {
                // Il permesso non è stato concesso, gestisci di conseguenza
                // Puoi informare l'utente o richiedere nuovamente il permesso
            }
        }

    private fun takePicture() {
        val imageCapture = ImageCapture.Builder().build()

        // Creiamo un file temporaneo per salvare l'immagine catturata
        val photoFile = createTempFile()

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Foto salvata con successo
                    // Puoi gestire la foto qui
                }

                override fun onError(exception: ImageCaptureException) {
                    // Errore durante il salvataggio della foto
                    // Puoi gestire l'errore qui
                }
            })
    }

    // Metodo per creare un file temporaneo dove salvare l'immagine catturata
    private fun createTempFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.shirts_fragment, container, false)

        //RecyclerView Initialization
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Get list of shirts
        var shirtsList: MutableList<ShirtBackend> = mutableListOf()
        apiGetShirts.getShirts().enqueue(object : Callback<GetShirtsResponse> {
            override fun onResponse(call: Call<GetShirtsResponse>, response: Response<GetShirtsResponse>) {
                Log.d("ShirtsFragment", "JSON Response: ${response.body()}")
                try {
                    // Access the result using response.body()
                    val result: GetShirtsResponse? = response.body()

                    // Check if the result is not null before accessing properties
                    result?.let {
                        val status = it.status
                        if (status == 200) {
                            shirtsList.addAll(it.shirts)
                            Log.d("ShirtsFragment", "Shirts added")
                            setupAdapter(shirtsList)
                        }
                        else {
                            Log.e("ShirtsFragment", "${it.status}")
                        }
                    }
                } catch (e: Exception) {
                    // Do nothing
                    Log.e("ShirtsFragment", "[ERROR] "+e.toString())
                }
            }
            override fun onFailure(call: Call<GetShirtsResponse>, t: Throwable) {
                Log.e("ShirtsFragment", "[ERR] ${t.message}")
            }
        })
        /*
        val shirtsList = listOf(
            Shirt(R.drawable.shirt_n1, "Maglietta 1"),
            Shirt(R.drawable.shirt_n2, "Maglietta 2"),
            Shirt(R.drawable.shirt_n3, "Maglietta 3")
        )*/
        /*
        var finalShirtsList: MutableList<Shirt> = mutableListOf()
        for (shirt in shirtsList) {
            val id = shirt.id
            val shirt_bytes = Base64.decode(shirt.shirt, Base64.DEFAULT)
            val shirt_bitmap: Bitmap? = BitmapFactory.decodeByteArray(shirt_bytes, 0, shirt_bytes!!.size)
            val shirt_name = shirt.shirtName

            val new_shirt = Shirt(id, shirt_bitmap, shirt_name)
            finalShirtsList.add(new_shirt)
        }

        // Inizializza e imposta l'adattatore
        shirtsAdapter = ShirtsAdapter(finalShirtsList, this)
        recyclerView.adapter = shirtsAdapter
        */

        return view
    }
    private fun setupAdapter(shirtsList: List<ShirtBackend>) {
        val finalShirtsList: MutableList<Shirt> = mutableListOf()

        for (shirt in shirtsList) {
            val id = shirt.id
            val shirt_bytes = Base64.decode(shirt.shirt, Base64.DEFAULT)
            val shirt_bitmap: Bitmap? = BitmapFactory.decodeByteArray(shirt_bytes, 0, shirt_bytes!!.size)
            val shirt_name = shirt.shirtName

            val new_shirt = Shirt(id, shirt_bitmap, shirt_name)
            finalShirtsList.add(new_shirt)
        }

        // Initialize and set the adapter
        shirtsAdapter = ShirtsAdapter(finalShirtsList, object : ShirtsAdapter.OnItemClickListener {
            override fun onItemClick(shirt: Shirt) {
                // Handle item click if needed
            }
        })
        recyclerView.adapter = shirtsAdapter
    }

    override fun onItemClick(shirt: Shirt) {
        // Gestisci il clic sulla card qui, ad esempio, apri la fotocamera
        openCamera()
    }

    private fun openCamera() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Se il permesso è già garantito, avvia la fotocamera
                takePicture()
            }
            else -> {
                // Se il permesso non è ancora stato concesso, richiedilo
                requestCameraPermission.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

}