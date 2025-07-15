package com.example.kkobakkobak.ui.medication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kkobakkobak.data.model.MedicationReminder
import com.example.kkobakkobak.databinding.ItemReminderBinding

class MedicationReminderAdapter(
    private val reminderList: MutableList<MedicationReminder>,
    private val onActionClick: (MedicationReminder) -> Unit // Handles both set and cancel actions
) : RecyclerView.Adapter<MedicationReminderAdapter.ReminderViewHolder>() {

    inner class ReminderViewHolder(private val binding: ItemReminderBinding) : RecyclerView.ViewHolder(binding.root) {
        val categoryIcon: TextView = binding.tvCategoryIcon
        val categoryTitle: TextView = binding.tvCategoryTitle
        val timeText: TextView = binding.tvTime
        val setButton: Button = binding.btnSet
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminderList[position]

        // Set icon and title based on category
        when (reminder.category) {
            "morning" -> {
                holder.categoryIcon.text = "☀️"
                holder.categoryTitle.text = "아침"
            }
            "lunch" -> {
                holder.categoryIcon.text = "🍚"
                holder.categoryTitle.text = "점심"
            }
            "dinner" -> {
                holder.categoryIcon.text = "🌙"
                holder.categoryTitle.text = "저녁"
            }
            "bedtime" -> {
                holder.categoryIcon.text = "🛏️"
                holder.categoryTitle.text = "취침 전"
            }
            else -> {
                holder.categoryIcon.text = ""
                holder.categoryTitle.text = reminder.category.capitalize() // Fallback
            }
        }

        val time = if (reminder.hour != -1 && reminder.minute != -1) {
            String.format("%02d:%02d", reminder.hour, reminder.minute)
        } else {
            "미설정"
        }
        val displayText = if (reminder.medicationName == "미설정") {
            time
        } else {
            "$time (${reminder.medicationName})"
        }
        holder.timeText.text = displayText

        holder.setButton.text = if (reminder.isActive) "취소" else "설정"

        holder.setButton.setOnClickListener {
            onActionClick(reminder)
        }
    }

    override fun getItemCount(): Int = reminderList.size
}