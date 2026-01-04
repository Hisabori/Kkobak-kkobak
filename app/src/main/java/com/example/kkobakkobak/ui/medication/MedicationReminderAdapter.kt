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
    private val onActionClick: (MedicationReminder) -> Unit,
    private val onItemClick: (MedicationReminder) -> Unit
) : ListAdapter<MedicationReminder, MedicationReminderAdapter.ReminderViewHolder>(ReminderDiffCallback()) {

    inner class ReminderViewHolder(private val binding: ItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reminder: MedicationReminder) {
            // 💡 1. 아이콘 및 시간대 설정 (카테고리 한글 대응)
            when (reminder.category.lowercase(Locale.getDefault())) {
                "morning", "아침" -> {
                    binding.tvCategoryIcon.text = "☀️"
                    binding.tvCategoryTitle.text = "아침"
                }
                "lunch", "점심" -> {
                    binding.tvCategoryIcon.text = "🍚"
                    binding.tvCategoryTitle.text = "점심"
                }
                "dinner", "저녁" -> {
                    binding.tvCategoryIcon.text = "🌙"
                    binding.tvCategoryTitle.text = "저녁"
                }
                "bedtime", "취침 전" -> {
                    binding.tvCategoryIcon.text = "🛏️"
                    binding.tvCategoryTitle.text = "취침 전"
                }
                else -> {
                    binding.tvCategoryIcon.text = "💊"
                    binding.tvCategoryTitle.text = reminder.category
                }
            }

            // 💡 2. 시간 표시 (모델의 time: String 필드 사용)
            // 기존의 hour, minute 대신 합쳐진 time 문자열을 그대로 사용하거나 가공함
            val timeDisplay = if (reminder.time.isNotBlank() && reminder.time.contains(":")) {
                reminder.time
            } else {
                "미설정"
            }

            // 💡 3. 약물 이름 표시 (medicationName -> medicineName 변경 반영)
            val medicineText = reminder.medicineName.takeIf {
                it != "미설정" && it.isNotBlank()
            } ?: "약물 미설정"

            // 💡 4. 활성화 상태에 따른 텍스트 설정
            binding.tvTime.text = if (reminder.isActive) {
                if (timeDisplay != "미설정") "$timeDisplay ($medicineText)" else "시간 미설정"
            } else {
                if (timeDisplay != "미설정") "$timeDisplay (비활성화됨)" else "시간 미설정"
            }

            // 💡 5. 버튼 및 클릭 리스너
            binding.btnSet.text = if (reminder.isActive) "취소" else "설정"

            binding.btnSet.setOnClickListener {
                onActionClick(reminder)
            }

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