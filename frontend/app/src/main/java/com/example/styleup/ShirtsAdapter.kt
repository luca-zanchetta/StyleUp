package com.example.styleup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShirtsAdapter(private val shirtsList: List<Shirt>, private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<ShirtsAdapter.ShirtViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShirtViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shirt, parent, false)
        return ShirtViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShirtViewHolder, position: Int) {
        val shirt = shirtsList[position]
        holder.shirtImage.setImageBitmap(shirt.shirt)
        holder.shirtName.text = shirt.shirt_name

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(shirt)
        }
    }

    override fun getItemCount(): Int {
        return shirtsList.size
    }

    class ShirtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shirtImage: ImageView = itemView.findViewById(R.id.shirtImage)
        val shirtName: TextView = itemView.findViewById(R.id.shirtName)
        // L'icona ic_shirt_icon è già gestita nel layout XML e non è necessario qui
    }

    interface OnItemClickListener {
        fun onItemClick(shirt: Shirt)
    }

}