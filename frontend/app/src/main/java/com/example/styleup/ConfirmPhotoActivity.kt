package com.example.styleup

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ConfirmPhotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirm_photo_activity)

        val photoImageView = findViewById<ImageView>(R.id.photoImageView)

        // Puoi impostare l'immagine nella ImageView utilizzando un'immagine o un percorso dell'immagine
        // Ad esempio, se stai passando un'immagine Bitmap:
        // val bitmap: Bitmap = ...
        // photoImageView.setImageBitmap(bitmap)
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val cancelButton = findViewById<ImageView>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            onCancelClick()
        }

        val acceptButton = findViewById<ImageView>(R.id.acceptButton)
        acceptButton.setOnClickListener {
            onAcceptClick()
        }
    }

    private fun onAcceptClick() {
        val intent = Intent(this, ProfileFragment::class.java)
        startActivity(intent)
    }

    private fun onCancelClick() {
        // Logica da eseguire quando viene cliccato il pulsante di accettazione
        // Ad esempio, puoi gestire il salvataggio dell'immagine e altre azioni
        // Chiudi l'attività se necessario
        finish()
    }
}