package com.example.styleup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class CreateAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val nameEditText: EditText = findViewById(R.id.editTextName)
        val emailEditText: EditText = findViewById(R.id.editTextEmailCreate)
        val passwordEditText: EditText = findViewById(R.id.editTextPasswordCreate)
        val confirmPasswordEditText: EditText = findViewById(R.id.editTextConfirmPassword)
        val signUpButton: Button = findViewById(R.id.btnConfirmSignUp)

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Qui puoi gestire la logica di registrazione utilizzando le variabili 'name', 'email', 'password', 'confirmPassword'

            Log.d("CreateAccountActivity", "Sign Up Button Clicked")

            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            //finish()
        }

        val backButton: ImageView = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }
    }
}