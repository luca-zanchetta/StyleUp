package com.example.styleup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ShirtsFragment: Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var shirtsAdapter: ShirtsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.shirts_fragment, container, false)

        //RecyclerView Initialization
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Sostituisci con la lista effettiva di magliette
        val shirtsList = listOf(
            Shirt(R.drawable.shirt_n1, "Maglietta 1"),
            Shirt(R.drawable.shirt_n2, "Maglietta 2"),
            Shirt(R.drawable.shirt_n3, "Maglietta 3")
        )

        // Inizializza e imposta l'adattatore
        shirtsAdapter = ShirtsAdapter(shirtsList)
        recyclerView.adapter = shirtsAdapter

        return view
    }
}