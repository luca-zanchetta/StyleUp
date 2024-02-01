package com.example.styleup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment

class FriendsFragment : Fragment() {

    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.friends_fragment, container, false)

        // Inizializza la variabile searchView
        searchView = view.findViewById(R.id.searchView)

        // Aggiungi un listener per la barra di ricerca
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Chiamato quando l'utente invia la query
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Chiamato quando la query cambia (ad esempio, l'utente sta digitando)
                // Puoi gestire la ricerca degli amici in base a newText qui
                return true
            }
        })
        return view
    }
}