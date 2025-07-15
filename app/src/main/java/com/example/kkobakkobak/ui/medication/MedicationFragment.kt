package com.example.kkobakkobak.ui.medication

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kkobakkobak.R
import com.example.kkobakkobak.alarm.AlarmScheduler
import com.example.kkobakkobak.databinding.FragmentMedicationBinding
import com.example.kkobakkobak.data.model.MedicationReminder
import java.util.Calendar

class MedicationFragment : Fragment() {
    private var _binding: FragmentMedicationBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MedicationReminderAdapter
    private val reminderList = mutableListOf<MedicationReminder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitialReminders()
        setupRecyclerView()
        updateReminderDisplay()
    }

    private fun setupInitialReminders() {
        reminderList.add(MedicationReminder("morning"))
        reminderList.add(MedicationReminder("lunch"))
        reminderList.add(MedicationReminder("dinner"))
        reminderList.add(MedicationReminder("bedtime"))
        loadReminderStates()
    }

    private fun loadReminderStates() {
        val prefs = requireContext().getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        reminderList.forEach { reminder ->
            reminder.hour = prefs.getInt("${reminder.category}_hour", -1)
            reminder.minute = prefs.getInt("${reminder.category}_minute", -1)
            reminder.medicationName = prefs.getString("${reminder.category}_med_name", "미설정") ?: "미설정"
            reminder.isActive = prefs.getBoolean("${reminder.category}_active", false)
        }
    }

    private fun setupRecyclerView() {
        adapter = MedicationReminderAdapter(reminderList) { reminder ->
            if (reminder.isActive) {
                cancelAlarm(reminder)
            } else {
                if (reminder.medicationName == "미설정") {
                    showMedicationNameDialog(reminder)
                } else {
                    showTimePicker(reminder)
                }
            }
        }
        binding.recyclerViewReminders.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewReminders.adapter = adapter
    }

    private fun showMedicationNameDialog(reminder: MedicationReminder) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("약 이름 설정")
        val input = EditText(requireContext())
        input.hint = "약 이름을 입력하세요 (예: 웰부트린)"
        input.setText(reminder.medicationName.takeIf { it != "미설정" })
        builder.setView(input)

        builder.setPositiveButton("확인") { dialog, _ ->
            val medName = input.text.toString().trim()
            if (medName.isNotEmpty()) {
                reminder.medicationName = medName
                saveReminderState(reminder)
                showTimePicker(reminder)
            } else {
                Toast.makeText(requireContext(), "약 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun showTimePicker(reminder: MedicationReminder) {
        val calendar = Calendar.getInstance()
        val initialHour = reminder.hour.takeIf { it != -1 } ?: calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = reminder.minute.takeIf { it != -1 } ?: calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                reminder.hour = selectedHour
                reminder.minute = selectedMinute
                reminder.isActive = true
                saveReminderState(reminder)

                val alarmScheduler = AlarmScheduler(requireContext())
                val alarmTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                    set(Calendar.SECOND, 0)
                    if (before(Calendar.getInstance())) {
                        add(Calendar.DATE, 1)
                    }
                }
                alarmScheduler.scheduleAlarm(alarmTime.timeInMillis, reminder.medicationName)

                updateReminderDisplay()
                Toast.makeText(requireContext(), "${reminder.category} 알람이 ${selectedHour}시 ${selectedMinute}분으로 설정되었습니다.", Toast.LENGTH_SHORT).show()
            },
            initialHour,
            initialMinute,
            false
        )
        timePickerDialog.show()
    }

    private fun saveReminderState(reminder: MedicationReminder) {
        val prefs = requireContext().getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putInt("${reminder.category}_hour", reminder.hour)
            .putInt("${reminder.category}_minute", reminder.minute)
            .putString("${reminder.category}_med_name", reminder.medicationName)
            .putBoolean("${reminder.category}_active", reminder.isActive)
            .apply()
    }

    private fun cancelAlarm(reminder: MedicationReminder) {
        val prefs = requireContext().getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .remove("${reminder.category}_hour")
            .remove("${reminder.category}_minute")
            .putString("${reminder.category}_med_name", "미설정")
            .putBoolean("${reminder.category}_active", false)
            .apply()

        val alarmScheduler = AlarmScheduler(requireContext())
        alarmScheduler.cancelAlarm(reminder.category)

        reminder.hour = -1
        reminder.minute = -1
        reminder.medicationName = "미설정"
        reminder.isActive = false

        updateReminderDisplay()
        Toast.makeText(requireContext(), "${reminder.category} 알람이 해제되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun updateReminderDisplay() {
        loadReminderStates()
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}