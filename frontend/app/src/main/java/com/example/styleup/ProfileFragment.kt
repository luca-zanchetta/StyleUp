package com.example.styleup

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class DeleteAccountRequest(val username: String?)
data class DeleteAccountResponse(val message: String, val status: Int)
data class GetProfileImageResponse(val profile_image: String?, val status: Int)
data class PostBackend2(val id: Int, val imageData: String, val username: String)
data class GetPostsResponse(val posts: List<PostBackend2>, val status: Int)
interface DeleteAccountAPI {
    @POST("deleteAccount")
    fun deleteAccount(@Body request: DeleteAccountRequest): Call<DeleteAccountResponse>
}
interface GetProfileImageAPI {
    @GET("getProfileImage")
    fun getProfileImage(@Query("username") username: String?): Call<GetProfileImageResponse>
}
interface GetPostsByUsernameAPI {
    @GET("getPostsByUsername")
    fun getPostsByUsername(@Query("username") username: String?):Call<GetPostsResponse>
}
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .addInterceptor(loggingInterceptor)
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl(backendURL)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
val apiService = retrofit.create(DeleteAccountAPI::class.java)
val apiService2 = retrofit.create(GetProfileImageAPI::class.java)
val getPostsByUsernameApiService = retrofit.create(GetPostsByUsernameAPI::class.java)

class ProfileFragment: Fragment() {

    private lateinit var noPostsMessage: TextView

    private lateinit var profileImage: ImageView
    private lateinit var usernameText: TextView

    private lateinit var settingsIcon: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var postRecyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.profile_fragment, container, false)
        settingsIcon = view.findViewById(R.id.settingsIcon)
        drawerLayout = view.findViewById(R.id.drawerLayout)
        navigationView = view.findViewById(R.id.navigationView)
        noPostsMessage = view.findViewById(R.id.noPostsMessage)

        val mainActivity = Intent(requireContext(), MainActivity::class.java)
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")

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
                    val intent = Intent(requireContext(), ModAccActivity::class.java)
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
        usernameText.text = username

        apiService2.getProfileImage(username).enqueue(object : Callback<GetProfileImageResponse> {
            override fun onResponse(call: Call<GetProfileImageResponse>, response: Response<GetProfileImageResponse>) {
                try {
                    // Access the result using response.body()
                    val result: GetProfileImageResponse? = response.body()

                    // Check if the result is not null before accessing properties
                    result?.let {
                        val status = it.status
                        if (status == 200) {
                            val profileImageByteArray = it.profile_image
                            if (profileImageByteArray != null) {
                                val originalBytes =
                                    Base64.decode(profileImageByteArray, Base64.DEFAULT)
                                setProfileImage(originalBytes)
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Do nothing
                    Log.e("ProfileFragment", e.toString())
                }
            }
            override fun onFailure(call: Call<GetProfileImageResponse>, t: Throwable) {
                // Do nothing
            }
        })

        //post
        // Inizializza la RecyclerView e l'adapter
        postRecyclerView = view.findViewById(R.id.postRecyclerView)
        getSamplePosts { posts ->
            postAdapter = PostAdapter(posts)
            postRecyclerView.adapter = postAdapter

            // Imposta il layout manager per la RecyclerView (ad esempio, LinearLayoutManager)
            postRecyclerView.layoutManager = LinearLayoutManager(requireContext())

            // Aggiorna la visibilità del messaggio in base all'elenco dei post
            updatePostList(posts)
        }

        return view
    }

    private fun updatePostList(samplePosts: List<Post>) {
        // Controlla se l'elenco dei post è vuoto e imposta la visibilità di conseguenza
        if (samplePosts.isEmpty()) {
            Log.d("ProfileFragment", "EMPTY")
            noPostsMessage.visibility = View.VISIBLE
        } else {
            Log.d("ProfileFragment", "NOT EMPTY")
            noPostsMessage.visibility = View.GONE
        }
    }

    private fun getSamplePosts(callback: (MutableList<Post>) -> Unit) {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")

        var posts: MutableList<Post> = mutableListOf()

        getPostsByUsernameApiService.getPostsByUsername(username).enqueue(object : Callback<GetPostsResponse> {
            override fun onResponse(call: Call<GetPostsResponse>, response: Response<GetPostsResponse>) {
                val result: GetPostsResponse? = response.body()

                // Check if the result is not null before accessing properties
                result?.let { it ->
                    val status = it.status
                    if (status == 200) {
                        for(post in it.posts) {
                            val id = post.id
                            val username = post.username
                            val encodedImage = post.imageData
                            val liked = false

                            val originalBytes = Base64.decode(encodedImage, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes!!.size)

                            val newPost = Post(id, username, bitmap, liked)
                            posts.add(newPost)
                        }
                    }
                    else {
                        Log.e("ProfileFragment", "No posts")
                    }
                }
                callback(posts)
            }
            override fun onFailure(call: Call<GetPostsResponse>, t: Throwable) {
                Log.e("ProfileFragment", "${t.message}")
            }

        })
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

    fun setProfileImage(imageByteArray: ByteArray?) {
        val bitmap: Bitmap? = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray!!.size)
        profileImage.setImageBitmap(bitmap)
    }

    fun setUsername(username: String?) {
        usernameText.text = username
    }
}