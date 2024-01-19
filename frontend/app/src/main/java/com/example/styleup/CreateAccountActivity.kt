package com.example.styleup

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log

data class Person(val username: String, val email: String, val password: String)

interface ApiService {
    @POST("register")
    fun createAccount(@Body person: Person): Call<CreateAccountResponse>
}

data class CreateAccountResponse(val message: String, val status: Int)

class CreateAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val nameEditText: EditText = findViewById(R.id.editTextName)
        val emailEditText: EditText = findViewById(R.id.editTextEmailCreate)
        val passwordEditText: EditText = findViewById(R.id.editTextPasswordCreate)
        val confirmPasswordEditText: EditText = findViewById(R.id.editTextConfirmPassword)
        val signUpButton: Button = findViewById(R.id.btnConfirmSignUp)

        // Call REST API logic
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        val person = Person("username", "email@example.com", "password")

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (password === confirmPassword) {
                apiService.createAccount(person).enqueue(object : Callback<CreateAccountResponse> {
                    override fun onResponse(call: Call<CreateAccountResponse>, response: Response<CreateAccountResponse>) {
                        if (response.isSuccessful) {
                            // Sarebbe più carino con un popup nella UI
                            Log.d("CreateAccount", "User registered successfully: ${response.body()}")
                        }
                    }

                    override fun onFailure(call: Call<CreateAccountResponse>, t: Throwable) {
                        // Sarebbe più carino con un popup nella UI
                        Log.e("CreateAccount", "Error: ${t.message}", t)
                    }
                })
            }
            else {
                // Sarebbe più carino con un popup nella UI
                Log.e("CreateAccount", "ERROR: The inserted passwords do not match.")
            }
        }
    }
}