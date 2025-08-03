package com.example.kkobakkobak.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kkobakkobak.R
import com.example.kkobakkobak.data.model.MedicationIntake

class LogHistoryAdapter(private val logs: List<MedicationIntake>) :
    RecyclerView.Adapter<LogHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val medName: TextView = view.findViewById(R.id.tv_medication_name)
        val timestamp: TextView = view.findViewById(R.id.tv_timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = logs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val log = logs[position]
        holder.medName.text = log.medicationName
        holder.timestamp.text = log.timestamp.toString() // 포맷팅 필요 시 따로 처리
    }
}
