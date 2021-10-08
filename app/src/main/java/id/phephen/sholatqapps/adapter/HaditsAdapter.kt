package id.phephen.sholatqapps.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.phephen.sholatqapps.R
import id.phephen.sholatqapps.model.Hadits

/**
 * Created by Phephen on 10/09/2021.
 */


class HaditsAdapter(private val items: List<Hadits>, private val onItemSelectedData: OnItemSelectedData): RecyclerView.Adapter<HaditsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HaditsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_doa_harian, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]
        holder.tvTitle.text = "Hadits no : " + data.number
        holder.lLContent.setOnClickListener {
            onItemSelectedData.onItemSelected(data)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tv_title_doa)
        var lLContent: LinearLayout = itemView.findViewById(R.id.lL_content)
    }

    interface OnItemSelectedData{
        fun onItemSelected(data: Hadits)
    }

}