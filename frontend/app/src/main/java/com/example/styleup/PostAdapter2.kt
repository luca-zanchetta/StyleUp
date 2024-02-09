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
class PostAdapter2(private val postList: List<Post>):
    RecyclerView.Adapter<PostAdapter2.PostViewHolder>()  {
    class PostViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postToolbar: Toolbar = itemView.findViewById(R.id.postToolbar)
        val postImage: ImageView = itemView.findViewById(R.id.postImage)
        val usernameText: TextView = itemView.findViewById(R.id.usernameText)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        var isLiked = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_2, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        // Carica i dati del post nella posizione specifica
        val currentPost = postList[position]

        // Imposta l'immagine del post (aggiungi la logica necessaria per caricare l'immagine)
        holder.postImage.setImageBitmap(currentPost.imageData)

        // Imposta il nome utente
        holder.usernameText.text = currentPost.username

        // Like button
        holder.likeButton.setOnClickListener {
            // Inverti lo stato del like
            holder.isLiked = !holder.isLiked

            // Aggiorna l'immagine del pulsante Like in base allo stato attuale
            if (holder.isLiked) {
                holder.likeButton.setImageResource(R.drawable.ic_like_filled) // Icona riempita del like
            } else {
                holder.likeButton.setImageResource(R.drawable.ic_like) // Icona del like vuota
            }
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}
