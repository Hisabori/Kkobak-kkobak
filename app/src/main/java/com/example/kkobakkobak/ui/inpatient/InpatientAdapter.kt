package com.example.kkobakkobak.ui.inpatient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kkobakkobak.R
import com.example.kkobakkobak.data.model.InpatientEntry
import java.time.format.DateTimeFormatter

class InpatientAdapter(private var items: List<InpatientEntry>) :
    RecyclerView.Adapter<InpatientAdapter.VH>() {

    private val df = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val date: TextView = v.findViewById(R.id.tv_date)
        val weekday: TextView = v.findViewById(R.id.tv_weekday)
        val count: TextView = v.findViewById(R.id.tv_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_inpatient, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val e = items[pos]
        h.date.text = e.date.format(df)
        h.weekday.text = e.weekday
        h.count.text = "${e.count}명"
    }

    override fun getItemCount() = items.size

    fun submit(list: List<InpatientEntry>) {
        items = list
        notifyDataSetChanged()
    }
}
