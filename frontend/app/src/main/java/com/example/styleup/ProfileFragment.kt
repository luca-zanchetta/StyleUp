package com.example.styleup

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import android.view.MenuItem
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
interface FriendItemClickListener {
    fun onRemoveFriendClicked(friendName: String)
}

class ProfileFragment: Fragment(), FriendItemClickListener {

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

        settingsIcon.setOnClickListener {
            // Apri il drawer quando viene cliccato l'icona delle impostazioni
            drawerLayout.openDrawer(navigationView)
        }

        // Aggiungi un listener per gestire le selezioni del menu a tendina laterale
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_logout -> {
                    // Esegui il logout
                    showConfirmationDialog()
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

        //post
        // Inizializza la RecyclerView e l'adapter
        postRecyclerView = view.findViewById(R.id.postRecyclerView)
        postAdapter = PostAdapter(getSamplePosts()) // Sostituisci con i dati reali dei post
        postRecyclerView.adapter = postAdapter

        // Imposta il layout manager per la RecyclerView (ad esempio, LinearLayoutManager)
        postRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Aggiorna la visibilità del messaggio in base all'elenco dei post
        updatePostList(getSamplePosts())

        //friends button
        val friendsButton = view.findViewById<Button>(R.id.friendsButton)
        friendsButton.setOnClickListener {
            showFriendList()
        }



        return view

    }

    private fun showFriendList() {
        val friends = listOf("Friend 1", "Friend 2", "Friend 3")
        val adapter = FriendListAdapter(friends, this)
        val listView = ListView(requireContext())
        listView.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setTitle("Friends")
            .setView(listView)
            .setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onRemoveFriendClicked(friendName: String) {
        showConfirmationDialog(friendName)
    }

    private fun showConfirmationDialog(friend: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete friend")
            .setMessage("Vuoi davvero eliminare $friend?")
            .setPositiveButton("Conferma") { dialog, which ->
                // Elimina l'amico
                Toast.makeText(requireContext(), "Amico $friend eliminato", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    private fun updatePostList(samplePosts: List<Post>) {
        // Controlla se l'elenco dei post è vuoto e imposta la visibilità di conseguenza
        if (samplePosts.isEmpty()) {
            noPostsMessage.visibility = View.VISIBLE
        } else {
            noPostsMessage.visibility = View.GONE
        }
    }

    private fun getSamplePosts(): List<Post> {
        // Sostituisci con la logica reale per ottenere i dati dei post
        // Restituisci una lista di oggetti Post con i dati desiderati
        // Ad esempio:
        // return listOf(Post(imageResourceId = R.drawable.sample_image1), ...)
        return emptyList()
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Conferma")
            .setMessage("Vuoi davvero eseguire questa azione?")
            .setPositiveButton("Conferma", DialogInterface.OnClickListener { dialog, which ->
                // Codice da eseguire se l'utente conferma
                // Aggiungi qui la logica desiderata
            })
            .setNegativeButton("Annulla", DialogInterface.OnClickListener { dialog, which ->
                // Codice da eseguire se l'utente annulla
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

class FriendListAdapter(
    private val friends: List<String>,
    private val clickListener: FriendItemClickListener
) : BaseAdapter() {

    override fun getCount(): Int {
        return friends.size
    }

    override fun getItem(position: Int): Any {
        return friends[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.friend_in_list, parent, false)
        val friendNameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        val removeFriendIcon = view.findViewById<ImageView>(R.id.removeFriendIcon)
        val likeButton: ImageView = view.findViewById<ImageView>(R.id.likeButton)
        var isLiked = false

        val friendName = friends[position]
        friendNameTextView.text = friendName

        // Imposta il click listener sull'icona di cancellazione
        removeFriendIcon.setOnClickListener {
            clickListener.onRemoveFriendClicked(friendName)
        }

        likeButton.setOnClickListener {
            // Inverti lo stato del like
            isLiked = !isLiked

            // Aggiorna l'immagine del pulsante Like in base allo stato attuale
            if (isLiked) {
                likeButton.setImageResource(R.drawable.ic_like_filled) // Icona riempita del like
            } else {
                likeButton.setImageResource(R.drawable.ic_like) // Icona del like vuota
            }
        }


        return view
    }
}
