package com.example.styleup

import android.content.Context
import android.content.Intent
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

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val message: String, val username: String, val status: Int)

interface LoginAPI {
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText: EditText = findViewById(R.id.editTextEmail)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)
        val loginButton: Button = findViewById(R.id.btnConfirmLogin)


        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(LoginAPI::class.java)

        // Get SharedPreferences instance (like a localStorage of react)
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)


        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            val intent = Intent(this, MainActivity::class.java)
            val loginRequest = LoginRequest(email, password)

            if (email != "" && password != "") {
                if (isValidEmail(email)) {
                    apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                            if (response.isSuccessful) {
                                // Sarebbe più carino con un popup nella UI
                                Log.d("Login", "Login successfully performed: ${response.body()}")

                                // Once the login operation is successful, I need to store my username for future interactions
                                // This username should be deleted at logout time
                                try {
                                    // Access the result using response.body()
                                    val result: LoginResponse? = response.body()

                                    // Check if the result is not null before accessing properties
                                    result?.let {
                                        val username: String = it.username

                                        // Store the username in the localStorage
                                        val editor = sharedPreferences.edit()
                                        editor.putString("username", username)
                                        editor.apply()
                                    }
                                } catch (e: Exception) {
                                    // Handle exceptions (e.g., network error, parsing error)
                                    Log.d("Login", e.toString())
                                }

                                startActivity(intent)
                            }
                            else {
                                // Sarebbe più carino con un popup nella UI
                                Log.e("Login", "ERROR: ${response.body()}")
                            }
                        }
                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            // Sarebbe più carino con un popup nella UI
                            Log.e("Login", "Error: ${t.message}", t)
                        }
                    })
                }
                else {
                    // Sarebbe più carino con un popup nella UI
                    Log.e("Login", "ERROR: The inserted email address is not valid. Try to follow the pattern try@example.com.")
                }
            }
            else {
                // Sarebbe più carino con un popup nella UI
                Log.e("Login", "ERROR: There is some empty field.")
            }
        }

    }
}

/*
Logout:
// Get SharedPreferences instance
val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

// Remove the username
val editor = sharedPreferences.edit()
editor.remove("username")
editor.apply()
 */