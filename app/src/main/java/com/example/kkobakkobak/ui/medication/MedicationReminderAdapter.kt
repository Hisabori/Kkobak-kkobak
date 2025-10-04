package com.example.kkobakkobak.ui.medication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kkobakkobak.data.model.MedicationReminder
import com.example.kkobakkobak.databinding.ItemReminderBinding
import java.util.Locale

class MedicationReminderAdapter(
    // 버튼 클릭 (설정/취소)
    private val onActionClick: (MedicationReminder) -> Unit,
    // 항목 전체 클릭 (시간/약물 설정)
    private val onItemClick: (MedicationReminder) -> Unit
) : ListAdapter<MedicationReminder, MedicationReminderAdapter.ReminderViewHolder>(ReminderDiffCallback()) {

    inner class ReminderViewHolder(private val binding: ItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reminder: MedicationReminder) {

            // 아이콘 및 시간대 설정
            when (reminder.category.lowercase(Locale.getDefault())) {
                "morning" -> {
                    binding.tvCategoryIcon.text = "☀️"
                    binding.tvCategoryTitle.text = "아침"
                }
                "lunch" -> {
                    binding.tvCategoryIcon.text = "🍚"
                    binding.tvCategoryTitle.text = "점심"
                }
                "dinner" -> {
                    binding.tvCategoryIcon.text = "🌙"
                    binding.tvCategoryTitle.text = "저녁"
                }
                "bedtime" -> {
                    binding.tvCategoryIcon.text = "🛏️"
                    binding.tvCategoryTitle.text = "취침 전"
                }
                else -> {
                    binding.tvCategoryIcon.text = ""
                    binding.tvCategoryTitle.text = reminder.category.replaceFirstChar { it.uppercase() }
                }
            }

            // 시간 및 약물 이름 표시
            val time = if (reminder.hour != -1 && reminder.minute != -1) {
                String.format(Locale.getDefault(), "%02d:%02d", reminder.hour, reminder.minute)
            } else {
                "미설정"
            }

            val medicationText = reminder.medicationName.takeIf { it != "미설정" && it.isNotBlank() } ?: "약물 미설정"

            binding.tvTime.text = if (reminder.isActive) {
                "$time (${medicationText})"
            } else {
                if (reminder.hour != -1) "$time (비활성화됨)" else "시간 미설정"
            }

            // 버튼 텍스트 설정
            binding.btnSet.text = if (reminder.isActive) "취소" else "설정"

            // 리스너 연결
            binding.btnSet.setOnClickListener {
                onActionClick(reminder)
            }

            // 항목 전체 클릭 리스너
            binding.root.setOnClickListener {
                onItemClick(reminder)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ReminderDiffCallback : DiffUtil.ItemCallback<MedicationReminder>() {
    override fun areItemsTheSame(oldItem: MedicationReminder, newItem: MedicationReminder): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MedicationReminder, newItem: MedicationReminder): Boolean {
        return oldItem == newItem
    }
}