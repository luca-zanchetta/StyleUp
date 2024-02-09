package com.example.styleup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class FriendProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friend_profile_activity)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putString("window", "friends")
            editor.apply()

            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
        }

        val friendName = intent.getStringExtra("friendName")
    }
}