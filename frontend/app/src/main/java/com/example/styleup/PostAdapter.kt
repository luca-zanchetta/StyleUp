package com.example.styleup

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import retrofit2.http.Body
import retrofit2.http.POST

data class Post(
    val id: Int,  // ID dell'immagine del post (R.drawable.example_image)
    val username: String,       // Nome utente dell'autore del post
    val imageData: Bitmap,
    val liked: Boolean
)
data class DeletePostRequest()
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
                    // Codice da eseguire se l'utente conferma l'eliminazione
                    // (ad esempio, rimuovere il post dalla lista)
                    val postID = currentPost.id

                }
                .setNegativeButton("NO", null)
                .show()
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}
