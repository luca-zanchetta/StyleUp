package com.example.styleup

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val backendURL: String = "http://10.0.2.2:5000"
//val backendURL: String = "http://lucazanchetta7.pythonanywhere.com"

val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl(backendURL)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

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