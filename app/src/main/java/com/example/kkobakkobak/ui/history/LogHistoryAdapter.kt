package com.example.kkobakkobak.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kkobakkobak.databinding.ItemLogHistoryBinding
import com.example.kkobakkobak.data.model.MedicationIntake

class LogHistoryAdapter(private val logs: List<MedicationIntake>) :
    RecyclerView.Adapter<LogHistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemLogHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    // 💡 누락된 필수 메서드 구현
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLogHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val log = logs[position]
        holder.binding.tvTimestamp.text = "${log.date} ${log.time}"
    }

    override fun getItemCount(): Int = logs.size
}