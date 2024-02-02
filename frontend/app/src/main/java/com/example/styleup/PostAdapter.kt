package com.example.styleup

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class Post(
    val id: Int,  // ID dell'immagine del post (R.drawable.example_image)
    val username: String,       // Nome utente dell'autore del post
    val imageData: Bitmap,
    val liked: Boolean
)

data class DeletePostRequest(val id: Int)
data class DeletePostResponse(val message: String, val status: Int)
interface DeletePostAPI {
    @POST("deletePost")
    fun deletePost(@Body request: DeletePostRequest): Call<DeletePostResponse>
}
class PostAdapter(private val postList: List<Post>):
    RecyclerView.Adapter<PostAdapter.PostViewHolder>()  {
    class PostViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postToolbar: Toolbar = itemView.findViewById(R.id.postToolbar)
        val deletePostButton: ImageView = itemView.findViewById(R.id.deletePostButton)
        val postImage: ImageView = itemView.findViewById(R.id.postImage)
        val usernameText: TextView = itemView.findViewById(R.id.usernameText)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        // Carica i dati del post nella posizione specifica
        val currentPost = postList[position]

        // Imposta l'immagine del post (aggiungi la logica necessaria per caricare l'immagine)
        holder.postImage.setImageBitmap(currentPost.imageData)

        // Imposta il nome utente
        holder.usernameText.text = currentPost.username

        // Aggiungi un listener per il click sul pulsante di eliminazione del post
        holder.deletePostButton.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("DELETE POST")
                .setMessage("Do you really want to delete this post?")
                .setPositiveButton("YES") { _, _ ->
                    val postID = currentPost.id

                    val request = DeletePostRequest(postID)
                    val deletePostApiService = retrofit.create(DeletePostAPI::class.java)
                    deletePostApiService.deletePost(request).enqueue(object : Callback<DeletePostResponse> {
                        override fun onResponse(call: Call<DeletePostResponse>, response: Response<DeletePostResponse>) {
                            try {
                                // Access the result using response.body()
                                val result: DeletePostResponse? = response.body()

                                // Check if the result is not null before accessing properties
                                result?.let {
                                    val status = it.status
                                    if (status == 200) {
                                        Log.d("PostAdapter", "OK")
                                        val ok = AlertDialog.Builder(holder.itemView.context)
                                        ok.setTitle("Message")
                                            .setMessage("${it.message}")
                                            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                                                val sharedPreferences = holder.itemView.context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                                val editor = sharedPreferences.edit()
                                                editor.putString("window", "profile")
                                                editor.apply()

                                                val intent = Intent(holder.itemView.context, FeedActivity::class.java)
                                                holder.itemView.context.startActivity(intent)
                                            })
                                            .show()
                                    }
                                    else {
                                        val ko = AlertDialog.Builder(holder.itemView.context)
                                        ko.setTitle("ERROR")
                                            .setMessage("${it.message}")
                                            .setPositiveButton("OK", DialogInterface.OnClickListener {dialog, which ->
                                                // Do nothing :)
                                            })
                                            .show()
                                    }
                                }
                            } catch (e: Exception) {
                                val ko = AlertDialog.Builder(holder.itemView.context)
                                ko.setTitle("ERROR")
                                    .setMessage("${e.toString()}")
                                    .setPositiveButton("OK", DialogInterface.OnClickListener {dialog, which ->
                                        // Do nothing :)
                                    })
                                    .show()
                            }
                        }
                        override fun onFailure(call: Call<DeletePostResponse>, t: Throwable) {
                            val ko = AlertDialog.Builder(holder.itemView.context)
                            ko.setTitle("ERROR")
                                .setMessage("${t.message}")
                                .setPositiveButton("OK", DialogInterface.OnClickListener {dialog, which ->
                                    // Do nothing :)
                                })
                                .show()
                        }
                    })

                }
                .setNegativeButton("NO", null)
                .show()
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}
