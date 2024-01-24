package com.example.styleup

import android.app.AlertDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ModAccActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mod_acc)

        val backButton: ImageView = findViewById(R.id.backButton)

        backButton.setOnClickListener{
            onBackPressed()
        }

        val confirmChangesButton: Button = findViewById(R.id.confirmChangesButton)
        confirmChangesButton.setOnClickListener {
            // Chiamare il metodo per confermare le modifiche
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmation")
                .setMessage("Your profile changes have been saved successfully.")
                .setPositiveButton("OK") { _, _ ->
                    // Torna alla schermata precedente
                    onBackPressed()
                }
                .show()
        }


    }


}
