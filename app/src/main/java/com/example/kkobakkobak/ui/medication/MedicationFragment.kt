package com.example.kkobakkobak.ui.medication

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kkobakkobak.R
import com.example.kkobakkobak.alarm.AlarmScheduler
import com.example.kkobakkobak.data.database.AppDatabase
import com.example.kkobakkobak.data.model.MedicationReminder
import com.example.kkobakkobak.databinding.FragmentMedicationBinding
import com.example.kkobakkobak.ui.history.MedicationHistoryActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class MedicationFragment : Fragment() {

    private var _binding: FragmentMedicationBinding? = null
    private val binding get() = _binding!!

    private lateinit var reminderAdapter: MedicationReminderAdapter
    private lateinit var db: AppDatabase
    private lateinit var alarmScheduler: AlarmScheduler

    private val initialCategories = listOf("morning", "lunch", "dinner", "bedtime")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initDependencies()
        initUI()
        initDefaultReminders()
        observeReminderChanges()
    }

    private fun initDependencies() {
        db = AppDatabase.getDatabase(requireContext())
        alarmScheduler = AlarmScheduler(requireContext())
    }

    private fun initUI() {
        val onActionClick: (MedicationReminder) -> Unit = { reminder ->
            if (reminder.isActive) cancelReminder(reminder)
            else showEditReminderDialog(reminder)
        }

        val onItemClick: (MedicationReminder) -> Unit = { reminder ->
            showEditReminderDialog(reminder)
        }

        reminderAdapter = MedicationReminderAdapter(onActionClick, onItemClick)
        binding.recyclerViewReminders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reminderAdapter
            itemAnimator = DefaultItemAnimator()
        }

        binding.tvTodayStatus.setOnClickListener {
            startActivity(Intent(requireContext(), MedicationHistoryActivity::class.java))
        }

        binding.tvViewHistory.setOnClickListener {
            startActivity(Intent(requireContext(), MedicationHistoryActivity::class.java))
        }
    }

    private fun initDefaultReminders() {
        lifecycleScope.launch {
            initialCategories.forEach { category ->
                if (db.medicationIntakeDao().getReminderByCategory(category) == null) {
                    val defaultReminder = createDefaultReminder(category)
                    db.medicationIntakeDao().insertReminder(defaultReminder)
                }
            }
            db.medicationIntakeDao().getAllReminders().collectLatest { reminders ->
                reminders.filter { it.isActive }.forEach { alarmScheduler.schedule(it) }
            }
        }
    }

    private fun createDefaultReminder(category: String) = MedicationReminder(
        category = category,
        medicationName = "어떤 약을 챙겨드릴까요? 💊",
        isActive = false,
        hour = when (category) {
            "morning" -> 9
            "lunch" -> 13
            "dinner" -> 18
            "bedtime" -> 22
            else -> -1
        },
        minute = 0
    )

    private fun observeReminderChanges() {
        lifecycleScope.launch {
            db.medicationIntakeDao().getAllReminders().collectLatest { reminders ->
                reminderAdapter.submitList(reminders)
            }
        }
    }

    private fun showEditReminderDialog(reminder: MedicationReminder) {
        val medNameInput = EditText(requireContext()).apply {
            hint = "어떤 약을 드시나요? (예: 비타민D)"
            if(reminder.medicationName != "어떤 약을 챙겨드릴까요? 💊") {
                setText(reminder.medicationName)
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("💊 ${getCategoryKoreanName(reminder.category)} 약 설정하기")
            .setView(medNameInput)
            .setPositiveButton("다음") { _, _ ->
                val medName = medNameInput.text.toString().trim()
                if (medName.isNotBlank()) {
                    showTimePicker(reminder.copy(medicationName = medName))
                } else {
                    Toast.makeText(context, "약 이름을 알려주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showTimePicker(reminder: MedicationReminder) {
        val context = context ?: return
        val calendar = Calendar.getInstance()
        val initialHour = if (reminder.hour != -1) reminder.hour else calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = if (reminder.minute != -1) reminder.minute else calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, hour, minute ->
                val updatedReminder = reminder.copy(hour = hour, minute = minute, isActive = true)
                saveAndScheduleReminder(updatedReminder)
            },
            initialHour, initialMinute, false
        ).show()
    }

    private fun saveAndScheduleReminder(reminder: MedicationReminder) {
        lifecycleScope.launch {
            db.medicationIntakeDao().updateReminder(reminder)
            alarmScheduler.schedule(reminder)
            if (isAdded) {
                val timeString = String.format(Locale.getDefault(), "%02d:%02d", reminder.hour, reminder.minute)
                val message = "✅ ${getCategoryKoreanName(reminder.category)} 약, 이제 '${timeString}'에 챙겨드릴게요!"
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cancelReminder(reminder: MedicationReminder) {
        lifecycleScope.launch {
            val updatedReminder = reminder.copy(isActive = false)
            db.medicationIntakeDao().updateReminder(updatedReminder)
            alarmScheduler.cancel(reminder)
            if (isAdded) {
                val message = "${getCategoryKoreanName(reminder.category)} 약 알림이 해제되었어요. 편안한 하루 보내세요."
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getCategoryKoreanName(category: String): String = when (category) {
        "morning" -> "아침"
        "lunch" -> "점심"
        "dinner" -> "저녁"
        "bedtime" -> "취침 전"
        else -> category.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
