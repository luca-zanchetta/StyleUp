package com.example.styleup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

fun isValidEmail(email: String): Boolean {
    val pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$")
    return pattern.matcher(email).matches()
}

data class Person(val username: String, val email: String, val password: String)
data class CreateAccountResponse(val message: String, val status: Int)

interface RegisterAPI {
    @POST("register")
    fun createAccount(@Body person: Person): Call<CreateAccountResponse>
}

class CreateAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val nameEditText: EditText = findViewById(R.id.editTextName)
        val emailEditText: EditText = findViewById(R.id.editTextEmailCreate)
        val passwordEditText: EditText = findViewById(R.id.editTextPasswordCreate)
        val confirmPasswordEditText: EditText = findViewById(R.id.editTextConfirmPassword)
        val signUpButton: Button = findViewById(R.id.btnConfirmSignUp)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        if (username != "") {
            val homepage = Intent(this, FeedActivity::class.java)
            startActivity(homepage)
        }

        // Call REST API logic
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(RegisterAPI::class.java)

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            val intent = Intent(this, LoginActivity::class.java)

            if (name != "" && email != "" && password != "" && confirmPassword != "") {
                if (password == confirmPassword) {
                    if (password.length >= 8) {
                        if(isValidEmail(email)) {
                            val person = Person(name, email, password)
                            apiService.createAccount(person).enqueue(object : Callback<CreateAccountResponse> {
                                override fun onResponse(call: Call<CreateAccountResponse>, response: Response<CreateAccountResponse>) {
                                    try {
                                        // Access the result using response.body()
                                        val result: CreateAccountResponse? = response.body()

                                        // Check if the result is not null before accessing properties
                                        result?.let {
                                            val status = it.status
                                            if (status == 200) {
                                                // Sarebbe più carino con un popup nella UI
                                                Log.d("CreateAccount", "User registered successfully: ${response.body()}")
                                                startActivity(intent)
                                            }
                                            else {
                                                // Sarebbe più carino con un popup nella UI
                                                Log.e("CreateAccount", "ERROR: ${response.body()}")
                                            }
                                        }
                                    } catch (e: Exception) {
                                        // Handle exceptions (e.g., network error, parsing error)
                                        Log.d("Login", e.toString())
                                    }
                                }
                                override fun onFailure(call: Call<CreateAccountResponse>, t: Throwable) {
                                    // Sarebbe più carino con un popup nella UI
                                    Log.e("CreateAccount", "Error: ${t.message}", t)
                                }
                            })
                        }
                        else {      // Email is not valid
                            // Sarebbe più carino con un popup nella UI
                            Log.e("CreateAccount", "ERROR: The inserted email address is not valid. Try to follow the pattern try@example.com.")
                        }
                    }
                    else {      // The password is not long enough
                        // Sarebbe più carino con un popup nella UI
                        Log.e("CreateAccount", "ERROR: The inserted password should be at least 8 characters long.")
                    }
                }
                else {      // The inserted passwords do not match
                    // Sarebbe più carino con un popup nella UI
                    Log.e("CreateAccount", "ERROR: The inserted passwords do not match.")
                }
            }
            else {      // There is some empty field
                // Sarebbe più carino con un popup nella UI
                Log.e("CreateAccount", "ERROR: There is some empty field.")
            }
        }
    }
}