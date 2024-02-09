package com.example.styleup

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
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
    val friendName = intent.getStringExtra("friendName")
    val profileImage: ImageView = findViewById(R.id.profileImage)
    val usernameText: TextView = findViewById(R.id.usernameText)

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

        getUser(friendName) {user ->
            profileImage.setImageBitmap(user.profileImage)
            usernameText.text = user.username

            getUserPosts(friendName) {postList ->

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
                            val backendUser = it.user

                            val returnUserUsername = backendUser.username
                            val data = Base64.decode(backendUser.profileImage, Base64.DEFAULT)
                            val returnUserProfileImage =
                                BitmapFactory.decodeByteArray(data, 0, data!!.size)

                            returnUser = User(returnUserUsername, returnUserProfileImage)
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