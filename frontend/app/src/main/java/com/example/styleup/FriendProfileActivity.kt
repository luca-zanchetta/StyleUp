package com.example.styleup

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class FriendProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friend_profile_activity)

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }

        val friendName = intent.getStringExtra("friendName")
        // Aggiungi il codice per visualizzare il profilo dell'amico utilizzando friendName
    }
}