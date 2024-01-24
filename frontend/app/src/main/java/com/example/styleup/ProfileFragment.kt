package com.example.styleup

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class DeleteAccountRequest(val username: String?)
data class DeleteAccountResponse(val message: String, val status: Int)
interface DeleteAccountAPI {
    @POST("deleteAccount")
    fun deleteAccount(@Body request: DeleteAccountRequest): Call<DeleteAccountResponse>
}

val retrofit = Retrofit.Builder()
    .baseUrl("http://10.0.2.2:5000/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
val apiService = retrofit.create(DeleteAccountAPI::class.java)

class ProfileFragment: Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var usernameText: TextView

    private lateinit var settingsIcon: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.profile_fragment, container, false)
        settingsIcon = view.findViewById(R.id.settingsIcon)
        drawerLayout = view.findViewById(R.id.drawerLayout)
        navigationView = view.findViewById(R.id.navigationView)

        val mainActivity = Intent(requireContext(), MainActivity::class.java)
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        settingsIcon.setOnClickListener {
            // Apri il drawer quando viene cliccato l'icona delle impostazioni
            drawerLayout.openDrawer(navigationView)
        }

        // Aggiungi un listener per gestire le selezioni del menu a tendina laterale
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_logout -> {
                    // Remove the username
                    val editor = sharedPreferences.edit()
                    editor.remove("username")
                    editor.apply()

                    startActivity(mainActivity)
                    true
                }
                R.id.menu_modify_profile -> {
                    // Modifica il profilo
                    val intent = Intent(requireContext(),ModAccActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_delete_profile -> {
                    // Elimina il profilo
                    showConfirmationDialog()
                    true
                }
                else -> false
            }
        }

        // Inizializza le variabili dell'immagine del profilo e dello username
        profileImage = view.findViewById(R.id.profileImage)
        usernameText = view.findViewById(R.id.usernameText)

        profileImage.setImageResource(R.drawable.default_profile_image)
        usernameText.text = "Username"

        return view
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete account")
            .setMessage("Do you really want to delete your account?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val username = sharedPreferences.getString("username", "")

                val request = DeleteAccountRequest(username)
                apiService.deleteAccount(request).enqueue(object : Callback<DeleteAccountResponse> {
                    override fun onResponse(call: Call<DeleteAccountResponse>, response: Response<DeleteAccountResponse>) {
                        val result: DeleteAccountResponse? = response.body()

                        // Check if the result is not null before accessing properties
                        result?.let { it ->
                            val status = it.status
                            if (status == 200) {
                                // Delete operation was successful
                                val ok = AlertDialog.Builder(requireContext())
                                ok.setTitle("Message")
                                    .setMessage("${it.message}")
                                    .setPositiveButton("OK", DialogInterface.OnClickListener {dialog, which ->
                                        // Do logout and come back to homepage
                                        val mainActivity = Intent(requireContext(), MainActivity::class.java)

                                        val editor = sharedPreferences.edit()
                                        editor.remove("username")
                                        editor.apply()

                                        startActivity(mainActivity)
                                    })
                                    .show()
                            }
                            else {
                                val ko = AlertDialog.Builder(requireContext())
                                ko.setTitle("ERROR")
                                    .setMessage("${it.message}")
                                    .setPositiveButton("OK", DialogInterface.OnClickListener {dialog, which ->
                                        // Do nothing :)
                                    })
                                    .show()
                            }
                        }
                    }
                    override fun onFailure(call: Call<DeleteAccountResponse>, t: Throwable) {
                        val ko = AlertDialog.Builder(requireContext())
                        ko.setTitle("ERROR")
                            .setMessage("${t.message}")
                            .setPositiveButton("OK", DialogInterface.OnClickListener {dialog, which ->
                                // Do nothing :)
                            })
                            .show()
                    }
                })
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                // Do nothing :)
            })
            .show()
    }

    // Aggiungi metodi per modificare l'immagine del profilo e lo username
    fun setProfileImage(imageResId: Int) {
        profileImage.setImageResource(imageResId)
    }

    fun setUsername(username: String) {
        usernameText.text = username
    }

}