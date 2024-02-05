package com.example.styleup

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.File
import java.util.Date
import java.util.Locale
import com.google.gson.annotations.SerializedName
import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView

data class ShirtBackend(val id: Int, val shirt: String, val shirtName: String)
data class GetShirtsResponse(val shirts: List<ShirtBackend>, val status: Int)
interface GetShirtsAPI {
    @GET("getShirts")
    fun getShirts(): Call<GetShirtsResponse>
}

val apiGetShirts = retrofit.create(GetShirtsAPI::class.java)

class ShirtsFragment: Fragment(), ShirtsAdapter.OnItemClickListener {

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

        // Get list of shirts
        var shirtsList: MutableList<ShirtBackend> = mutableListOf()
        apiGetShirts.getShirts().enqueue(object : Callback<GetShirtsResponse> {
            override fun onResponse(call: Call<GetShirtsResponse>, response: Response<GetShirtsResponse>) {
                Log.d("ShirtsFragment", "JSON Response: ${response.body()}")
                try {
                    // Access the result using response.body()
                    val result: GetShirtsResponse? = response.body()

                    // Check if the result is not null before accessing properties
                    result?.let {
                        val status = it.status
                        if (status == 200) {
                            shirtsList.addAll(it.shirts)
                            Log.d("ShirtsFragment", "Shirts added")
                            setupAdapter(shirtsList)
                        }
                        else {
                            Log.e("ShirtsFragment", "${it.status}")
                        }
                    }
                } catch (e: Exception) {
                    // Do nothing
                    Log.e("ShirtsFragment", "[ERROR] "+e.toString())
                }
            }
            override fun onFailure(call: Call<GetShirtsResponse>, t: Throwable) {
                Log.e("ShirtsFragment", "[ERR] ${t.message}")
                // retry here
            }
        })

        return view
    }
    private fun setupAdapter(shirtsList: List<ShirtBackend>) {
        val finalShirtsList: MutableList<Shirt> = mutableListOf()

        for (shirt in shirtsList) {
            val id = shirt.id
            val shirt_bytes = Base64.decode(shirt.shirt, Base64.DEFAULT)
            val shirt_bitmap: Bitmap? = BitmapFactory.decodeByteArray(shirt_bytes, 0, shirt_bytes!!.size)
            val shirt_name = shirt.shirtName

            val new_shirt = Shirt(id, shirt_bitmap, shirt_name)
            finalShirtsList.add(new_shirt)
        }

        // Initialize and set the adapter
        shirtsAdapter = ShirtsAdapter(finalShirtsList, object : ShirtsAdapter.OnItemClickListener {
            override fun onItemClick(shirt: Shirt) {
                val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putInt("shirtId", shirt.id)
                editor.apply()

                val intent = Intent(requireContext(), CameraFragment::class.java)
                startActivity(intent)
            }
        })
        recyclerView.adapter = shirtsAdapter
    }

    override fun onItemClick(shirt: Shirt) {
        // Necessary for avoiding errors; handled in a different part of the code
    }

}