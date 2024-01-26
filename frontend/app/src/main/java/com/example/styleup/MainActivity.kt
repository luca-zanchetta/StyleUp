package com.example.styleup

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

val backendURL: String = "http://10.0.2.2:5000"
val webBackendURL: String = "lucazanchetta7.pythonanywhere.com"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        if (username != "") {
            val homepage = Intent(this, FeedActivity::class.java)
            startActivity(homepage)
        }

        val loginButton: Button = findViewById(R.id.btnLogin)
        val createAccountButton: Button = findViewById(R.id.btnCreateAccount)

        loginButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        createAccountButton.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }
}