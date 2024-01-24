package com.example.styleup

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class FeedActivity: AppCompatActivity() {

    private lateinit var icon1: ImageView
    private lateinit var icon2: ImageView
    private lateinit var icon3: ImageView
    private lateinit var icon4: ImageView

    private lateinit var toolbar: Toolbar

    private lateinit var mainFragmentContainer: FrameLayout
    private lateinit var fragmentManager: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)


        val notificationIcon: ImageView = findViewById(R.id.notificationIcon)
        notificationIcon.setOnClickListener {
            showConfirmationDialog()
        }

        mainFragmentContainer = findViewById(R.id.mainFragmentContainers)
        fragmentManager = supportFragmentManager

        val shirtsFragment = ShirtsFragment()
        setMainFragment(shirtsFragment)

        icon1 = findViewById(R.id.icon1)
        icon2 = findViewById(R.id.icon2)
        icon3 = findViewById(R.id.icon3)
        icon4 = findViewById(R.id.icon4)

        icon1.setOnClickListener {
            val friendsFragment = FriendsFragment()
            setMainFragment(friendsFragment)
        }

        icon2.setOnClickListener {
            setMainFragment(ShirtsFragment())
        }

        icon3.setOnClickListener {
            val profileFragment = ProfileFragment()
            setMainFragment(profileFragment)
        }

        icon4.setOnClickListener {
            val mapFragment = MapFragment()
            setMainFragment(mapFragment)
        }

    }

    private fun setMainFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mainFragmentContainers, fragment)
        transaction.commit()
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
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
}