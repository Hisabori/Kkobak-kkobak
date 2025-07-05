package com.example.kkobakkkobak

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LogHistoryAdapter(private val logs: List<MedicationLog>) : RecyclerView.Adapter<LogHistoryAdapter.LogViewHolder>() {

    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.tv_log_date)
        val memoTextView: TextView = view.findViewById(R.id.tv_log_memo)
        val moodImageView: ImageView = view.findViewById(R.id.iv_log_mood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = logs[position]
        holder.dateTextView.text = log.date
        holder.memoTextView.text = log.memo

        val moodIcon = when (log.mood) {
            1 -> R.drawable.ic_mood_good
            2 -> R.drawable.ic_mood_neutral
            3 -> R.drawable.ic_mood_bad
            else -> 0
        }
        if (moodIcon != 0) {
            holder.moodImageView.setImageResource(moodIcon)
        }
    }

    override fun getItemCount() = logs.size
}