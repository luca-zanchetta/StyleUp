package com.example.styleup

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

data class User(val username: String, val profileImage: Bitmap)
data class UserBackend(val username: String, val profileImage: String)
data class GetUserByUsernameResponse(val user: UserBackend, val status: Int)
interface GetUserByUsernameAPI {
    @GET("getUserByUsername")
    fun getUserByUsername(@Query("username") username: String?): Call<GetUserByUsernameResponse>
}
class FriendProfileActivity : AppCompatActivity() {
    private lateinit var friendName: String
    private lateinit var profileImage: ImageView
    private lateinit var usernameText: TextView

    private lateinit var postRecyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter2
    private lateinit var noPostsMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friend_profile_activity)

        profileImage = findViewById(R.id.profileImage)
        usernameText = findViewById(R.id.usernameText)
        noPostsMessage = findViewById(R.id.noPostsMessage)
        friendName = intent.getStringExtra("friendName").toString()

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putString("window", "friends")
            editor.apply()

            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
        }

        getUser(friendName) { user ->
            profileImage.setImageBitmap(user.profileImage)
            usernameText.text = friendName

            postRecyclerView = findViewById(R.id.postRecyclerView)

            getUserPosts(friendName) { postList ->
                postAdapter = PostAdapter2(this, postList)
                postRecyclerView.adapter = postAdapter

                // Imposta il layout manager per la RecyclerView (ad esempio, LinearLayoutManager)
                postRecyclerView.layoutManager = LinearLayoutManager(this)

                // Aggiorna la visibilità del messaggio in base all'elenco dei post
                updatePostList(postList)
            }
        }
    }

    private fun getUser(username: String?, callback: (User) -> Unit) {
        val getUserByUsernameApiService = retrofit.create(GetUserByUsernameAPI::class.java)
        getUserByUsernameApiService.getUserByUsername(username).enqueue(object : Callback<GetUserByUsernameResponse> {
            override fun onResponse(call: Call<GetUserByUsernameResponse>, response: Response<GetUserByUsernameResponse>) {
                try {
                    // Access the result using response.body()
                    val result: GetUserByUsernameResponse? = response.body()
                    lateinit var returnUser: User

                    // Check if the result is not null before accessing properties
                    result?.let {
                        val status = it.status
                        if (status == 200) {
                            Log.d("FriendProfileActivity", "OK")
                            val backendUser = it.user

                            val returnUserUsername = backendUser.username
                            if(backendUser.profileImage != "") {
                                val data = Base64.decode(backendUser.profileImage, Base64.DEFAULT)
                                val returnUserProfileImage =
                                    BitmapFactory.decodeByteArray(data, 0, data!!.size)

                                returnUser = User(returnUserUsername, returnUserProfileImage)
                            }
                            else {
                                val drawable = ContextCompat.getDrawable(this@FriendProfileActivity, R.drawable.default_profile_image)
                                val returnUserProfileImage = drawableToBitmap(drawable)

                                returnUser = User(returnUserUsername, returnUserProfileImage)
                            }


                        }
                        else {
                            Log.e("FriendProfileActivity", "status: ${status}")
                        }
                    }
                    callback(returnUser)
                } catch (e: Exception) {
                    // Do nothing
                    Log.e("ProfileFragment", e.toString())
                }
            }
            override fun onFailure(call: Call<GetUserByUsernameResponse>, t: Throwable) {
                // Do nothing
            }
        })
    }

    private fun getUserPosts(username: String?, callback: (MutableList<Post>) -> Unit) {
        var posts: MutableList<Post> = mutableListOf()
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val loggedUser = sharedPreferences.getString("username", "")

        getPostsByUsernameApiService.getPostsByUsername(username, loggedUser).enqueue(object : Callback<GetPostsResponse> {
            override fun onResponse(
                call: Call<GetPostsResponse>,
                response: Response<GetPostsResponse>
            ) {
                val result: GetPostsResponse? = response.body()

                // Check if the result is not null before accessing properties
                result?.let { it ->
                    val status = it.status
                    if (status == 200) {
                        for (post in it.posts) {
                            val id = post.id
                            val username = post.username
                            val encodedImage = post.imageData
                            val likes = post.likes
                            val likedByLoggedUser = post.likedByLoggedUser

                            val originalBytes = Base64.decode(encodedImage, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(
                                originalBytes,
                                0,
                                originalBytes!!.size
                            )

                            val newPost = Post(id, username, bitmap, likes, likedByLoggedUser)
                            posts.add(newPost)
                        }
                    } else {
                        Log.e("ProfileFragment", "No posts")
                    }
                }
                callback(posts)
            }

            override fun onFailure(call: Call<GetPostsResponse>, t: Throwable) {
                Log.e("FriendProfileActivity", "${t.message}")
            }
        })
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

    private fun drawableToBitmap(drawable: Drawable?): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable!!.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable!!.setBounds(0, 0, canvas.width, canvas.height)
        drawable!!.draw(canvas)

        return bitmap
    }
}