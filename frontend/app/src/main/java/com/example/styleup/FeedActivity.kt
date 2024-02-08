package com.example.styleup

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val fragmentSharedPreferences = sharedPreferences.getString("window", "")

        val notificationIcon: ImageView = findViewById(R.id.notificationIcon)
        notificationIcon.setOnClickListener {
            showNotificationMenu(it)
        }

        mainFragmentContainer = findViewById(R.id.mainFragmentContainers)
        fragmentManager = supportFragmentManager

        val shirtsFragment = ShirtsFragment()
        setMainFragment(shirtsFragment)

        if (fragmentSharedPreferences == "profile") {
            val editor = sharedPreferences.edit()
            editor.remove("window")
            editor.apply()

            val profileFragment = ProfileFragment()
            val username = sharedPreferences.getString("username", "")

            // Call for updating the UI
            apiService2.getProfileImage(username).enqueue(object :
                Callback<GetProfileImageResponse> {
                override fun onResponse(call: Call<GetProfileImageResponse>, response: Response<GetProfileImageResponse>) {
                    try {
                        // Access the result using response.body()
                        val result: GetProfileImageResponse? = response.body()

                        // Check if the result is not null before accessing properties
                        result?.let {
                            val status = it.status
                            if (status == 200) {
                                Log.d("FeedActivity", "RESPONSE OK")
                                val profileImageByteArray = it.profile_image
                                if (profileImageByteArray != null) {
                                    val originalBytes =
                                        Base64.decode(profileImageByteArray, Base64.DEFAULT)
                                    profileFragment.setProfileImage(originalBytes)
                                    profileFragment.setUsername(username)
                                    Log.d("FeedActivity", "UI updated.")
                                }
                                else {
                                    Log.e("FeedActivity", "profileImageByteArray is null!")
                                }
                            }
                            else {
                                Log.e("FeedActivity", "${status}")
                            }
                        }
                    } catch (e: Exception) {
                        // Do nothing
                        Log.e("FeedActivity", e.toString())
                    }
                }
                override fun onFailure(call: Call<GetProfileImageResponse>, t: Throwable) {
                    // Do nothing
                    Log.e("FeedActivity", "${t.message}")
                }
            })

            setMainFragment(profileFragment)
        }

        if(fragmentSharedPreferences == "friends") {
            val editor = sharedPreferences.edit()
            editor.remove("window")
            editor.apply()

            val friendsFragment = FriendsFragment()
            setMainFragment(friendsFragment)
        }

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
            //val mapFragment = MapFragment()
            //setMainFragment(mapFragment)
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setMainFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mainFragmentContainers, fragment)
        transaction.commit()
    }

    private fun showNotificationMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.menu_notifications)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.notification_item_1 -> {
                    // Perform action when notification item is clicked
                    Toast.makeText(this, "Notification clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                // Add more cases for other menu items if needed
                else -> false
            }
        }

        popupMenu.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_notifications, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.notification_item_1 -> {
                // Handle notification menu item click here if needed
                Toast.makeText(this, "Notification clicked", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}