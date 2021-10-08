package id.phephen.sholatqapps.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.phephen.sholatqapps.R
import id.phephen.sholatqapps.model.ModelAyat

/**
 * Created by Phephen on 30/08/2021.
 */
class AyatAdapter(private val items: List<ModelAyat>) : RecyclerView.Adapter<AyatAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_ayat, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]
        holder.tvNomorAyat.text = data.nomor
        holder.tvArabic.text = data.arab
        holder.tvTerjemahan.text = data.indo
    }

    override fun getItemCount(): Int {
        return items.size
    }

    //Class Holder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvNomorAyat: TextView = itemView.findViewById(R.id.tvNomorAyat)
        var tvArabic: TextView = itemView.findViewById(R.id.tvArabic)
        var tvTerjemahan: TextView = itemView.findViewById(R.id.tvTerjemahan)

    }
}