package com.example.styleup

import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import java.io.File
import java.util.Date
import java.util.Locale

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

        // Sostituisci con la lista effettiva di magliette
        val shirtsList = listOf(
            Shirt(R.drawable.shirt_n1, "Maglietta 1"),
            Shirt(R.drawable.shirt_n2, "Maglietta 2"),
            Shirt(R.drawable.shirt_n3, "Maglietta 3")
        )

        // Inizializza e imposta l'adattatore
        shirtsAdapter = ShirtsAdapter(shirtsList, this)
        recyclerView.adapter = shirtsAdapter

        return view
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