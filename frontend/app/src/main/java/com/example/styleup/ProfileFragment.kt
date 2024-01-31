package com.example.styleup

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
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



        return view

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