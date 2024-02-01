package com.example.styleup

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView

data class Post(
    val imageResourceId: Int,  // ID dell'immagine del post (R.drawable.example_image)
    val username: String,       // Nome utente dell'autore del post
    val liked: Boolean
)

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
        holder.postImage.setImageResource(currentPost.imageResourceId)

        // Imposta il nome utente
        holder.usernameText.text = currentPost.username

        // Aggiungi un listener per il click sul pulsante di eliminazione del post
        holder.deletePostButton.setOnClickListener {
            showDeleteConfirmationDialog(holder.itemView.context)
        }
    }

    private fun showDeleteConfirmationDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Conferma eliminazione")
            .setMessage("Sei sicuro di voler eliminare questo post?")
            .setPositiveButton("Conferma") { _, _ ->
                // Codice da eseguire se l'utente conferma l'eliminazione
                // (ad esempio, rimuovere il post dalla lista)
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}
