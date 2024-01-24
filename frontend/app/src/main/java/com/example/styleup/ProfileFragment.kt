package com.example.styleup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class ProfileFragment: Fragment() {

    private lateinit var settingsIcon: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.profile_fragment, container, false)
        settingsIcon = view.findViewById(R.id.settingsIcon)
        drawerLayout = view.findViewById(R.id.drawerLayout)
        navigationView = view.findViewById(R.id.navigationView)

        settingsIcon.setOnClickListener {
            // Apri il drawer quando viene cliccato l'icona delle impostazioni
            drawerLayout.openDrawer(navigationView)
        }

        // Aggiungi un listener per gestire le selezioni del menu a tendina laterale
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_logout -> {
                    // Esegui il logout
                    // ...
                    true
                }
                R.id.menu_modify_profile -> {
                    // Modifica il profilo
                    // ...
                    true
                }
                R.id.menu_delete_profile -> {
                    // Elimina il profilo
                    // ...
                    true
                }
                else -> false
            }
        }

        return view

    }


}