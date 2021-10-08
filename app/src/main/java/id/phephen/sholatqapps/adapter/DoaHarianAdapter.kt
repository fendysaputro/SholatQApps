package id.phephen.sholatqapps.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.phephen.sholatqapps.R
import id.phephen.sholatqapps.model.ModelDoaHarian

/**
 * Created by Phephen on 02/09/2021.
 */
class DoaHarianAdapter(private val items: List<ModelDoaHarian>, private val onSelectedData: OnSelectedData) : RecyclerView.Adapter<DoaHarianAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_doa_harian, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]
        holder.tvTitle_doa.text = data.title
        holder.lLContent.setOnClickListener {
            onSelectedData.onSelected(data)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle_doa: TextView = itemView.findViewById(R.id.tv_title_doa)
        var lLContent: LinearLayout = itemView.findViewById(R.id.lL_content)
    }

    interface OnSelectedData{
        fun onSelected(data: ModelDoaHarian)
    }

}