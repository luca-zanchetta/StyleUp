package com.example.styleup

import android.content.Intent
import android.os.Bundle
import android.view.TextureView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class CameraFragment : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_fragment)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val captureButton = findViewById<Button>(R.id.captureButton)
        captureButton.setOnClickListener{
            val intent = Intent(this, ConfirmPhotoActivity::class.java)
            startActivity(intent)
        }
    }


}