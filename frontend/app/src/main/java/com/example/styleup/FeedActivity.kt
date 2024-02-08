package com.example.styleup

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
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
            openOptionsMenu()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_notifications, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("FeedActivity", "Menù open")
        return when (item.itemId) {
            R.id.notificationIcon -> {
                // Apri la tendina di menu delle notifiche
                val popupMenu = PopupMenu(this, findViewById(R.id.notificationIcon))
                popupMenu.menuInflater.inflate(R.menu.menu_notifications, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    // Gestisci il clic sugli elementi del menu delle notifiche
                    when (menuItem.itemId) {
                        R.id.notification_item_1 -> {
                            // Azione per l'elemento di menu 1
                            Log.d("FeedActivity", "Notification Item 1 selected")
                            true
                        }
                        R.id.notification_item_2 -> {
                            // Azione per l'elemento di menu 2
                            Log.d("FeedActivity", "Notification Item 2 selected")
                            true
                        }
                        // Aggiungi altri casi per gli altri elementi di menu, se necessario
                        else -> false
                    }
                }
                popupMenu.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}