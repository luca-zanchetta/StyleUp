package com.example.styleup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FriendsFragment : Fragment() {

    private lateinit var searchView: SearchView

    private lateinit var friendRecyclerView: RecyclerView
    private lateinit var friendAdapter: FriendAdapter

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


        // Inizializza la RecyclerView
        friendRecyclerView = view.findViewById(R.id.friendRecyclerView)
        friendAdapter = FriendAdapter(getFriendList()) { friendName ->
            // Gestisci il clic sulla card dell'amico
            openFriendProfileActivity(friendName)
        }
        friendRecyclerView.adapter = friendAdapter
        friendRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    // Metodo per ottenere la lista degli amici (sostituiscilo con i tuoi dati reali)
    private fun getFriendList(): List<String> {
        return listOf("Friend 1", "Friend 2", "Friend 3")
    }

    // Metodo per aprire l'activity del profilo dell'amico
    private fun openFriendProfileActivity(friendName: String) {
        val intent = Intent(requireContext(), FriendProfileActivity::class.java)
        intent.putExtra("friendName", friendName)
        startActivity(intent)
    }
}