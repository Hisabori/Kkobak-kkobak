package com.example.kkobakkobak.ui.medication

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kkobakkobak.R
import com.example.kkobakkobak.alarm.AlarmScheduler
import com.example.kkobakkobak.data.database.AppDatabase
import com.example.kkobakkobak.databinding.FragmentMedicationBinding
import com.example.kkobakkobak.data.model.MedicationReminder
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

    // 초기 설정할 복용 시간대 목록
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

        db = AppDatabase.getDatabase(requireContext())
        alarmScheduler = AlarmScheduler(requireContext())

        // 기존 SharedPreferences 로직 제거 및 Room 기반 초기화
        setupInitialReminders()
        setupRecyclerView()
        observeReminders()

        // 기존 기능 유지
        binding.tvTodayStatus.setOnClickListener{
            startActivity(Intent(requireContext(), MedicationHistoryActivity::class.java))
        }

        binding.tvViewHistory.setOnClickListener {
            startActivity(Intent(requireContext(), MedicationHistoryActivity::class.java))
        }
    }

    // DB에 기본 알림 카테고리(아침/점심/저녁/취침전)가 없으면 추가
    private fun setupInitialReminders() {
        lifecycleScope.launch {
            initialCategories.forEach { category ->
                if (db.medicationIntakeDao().getReminderByCategory(category) == null) {
                    val defaultReminder = MedicationReminder(
                        category = category,
                        medicationName = "미설정",
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
                    // Room에 초기 데이터 삽입
                    db.medicationIntakeDao().insertReminder(defaultReminder)
                }
            }
            // 알람이 비활성화된 상태에서 앱이 종료/재시작되면 알람을 다시 등록해야 함 (BootReceiver에서 처리 권장)
            // 현재 Fragment에서는 활성화된 알림만 다시 스케줄링
            db.medicationIntakeDao().getAllReminders().collectLatest { reminders ->
                reminders.filter { it.isActive }.forEach { alarmScheduler.schedule(it) }
            }
        }
    }

    private fun setupRecyclerView() {
        // 버튼 클릭 (설정/취소) 처리: 상태를 반전시키고 다이얼로그를 띄우거나 취소
        val onActionClick: (MedicationReminder) -> Unit = { reminder ->
            if (reminder.isActive) {
                cancelReminder(reminder)
            } else {
                showTimeAndMedicationDialog(reminder)
            }
        }

        // 항목 전체 클릭 (시간/약물 설정) 처리
        val onItemClick: (MedicationReminder) -> Unit = { reminder ->
            showTimeAndMedicationDialog(reminder)
        }

        reminderAdapter = MedicationReminderAdapter(onActionClick, onItemClick)

        binding.recyclerViewReminders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reminderAdapter
        }
    }

    // Room에서 알림 목록을 관찰하고 RecyclerView 업데이트
    private fun observeReminders() {
        lifecycleScope.launch {
            // Room의 Flow를 사용해 DB 변경 시 자동 업데이트
            db.medicationIntakeDao().getAllReminders().collectLatest { reminders ->
                reminderAdapter.submitList(reminders)
            }
        }
    }

    // 알림 설정 다이얼로그 표시 (약 이름 입력 -> 시간 선택)
    private fun showTimeAndMedicationDialog(reminder: MedicationReminder) {
        val medNameInput = EditText(requireContext()).apply {
            hint = "약 이름을 입력하세요 (예: 웰부트린, 콘서타)"
            setText(reminder.medicationName.takeIf { it != "미설정" })
        }

        val medNameBuilder = AlertDialog.Builder(requireContext())
            .setTitle("${getCategoryKoreanName(reminder.category)} 복약 설정")
            .setView(medNameInput)
            .setPositiveButton("다음") { dialog, _ ->
                val medName = medNameInput.text.toString().trim()
                if (medName.isEmpty()) {
                    Toast.makeText(requireContext(), "약 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    val updatedReminder = reminder.copy(medicationName = medName)
                    showTimePicker(updatedReminder) // 시간 선택기로 이동
                }
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ -> dialog.cancel() }

        medNameBuilder.show()
    }

    // 시간 선택기 표시
    private fun showTimePicker(reminder: MedicationReminder) {
        val initialHour = reminder.hour.takeIf { it != -1 } ?: Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val initialMinute = reminder.minute.takeIf { it != -1 } ?: Calendar.getInstance().get(Calendar.MINUTE)

        TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val updatedReminder = reminder.copy(
                    hour = selectedHour,
                    minute = selectedMinute,
                    isActive = true // 시간 설정 완료 시 활성화
                )
                saveAndScheduleReminder(updatedReminder)
            },
            initialHour,
            initialMinute,
            false // 24시간 형식 비활성화
        ).show()
    }

    // DB 저장 및 알람 스케줄링
    private fun saveAndScheduleReminder(reminder: MedicationReminder) {
        lifecycleScope.launch {
            db.medicationIntakeDao().updateReminder(reminder)
            alarmScheduler.schedule(reminder)

            val timeString = String.format(Locale.getDefault(), "%02d:%02d", reminder.hour, reminder.minute)
            Toast.makeText(requireContext(),
                "${getCategoryKoreanName(reminder.category)} 알람이 '$timeString' 으로 설정되었습니다. 앱이 종료되어도 알람은 유지됩니다.",
                Toast.LENGTH_LONG).show()
        }
    }

    // 알림 취소 로직
    private fun cancelReminder(reminder: MedicationReminder) {
        val updatedReminder = reminder.copy(
            isActive = false
        )

        lifecycleScope.launch {
            db.medicationIntakeDao().updateReminder(updatedReminder)
            alarmScheduler.cancel(reminder) // 기존 알람 취소

            Toast.makeText(requireContext(),
                "${getCategoryKoreanName(reminder.category)} 알람이 취소되었습니다. 다시 설정하려면 '설정' 버튼을 누르세요.",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun getCategoryKoreanName(category: String): String {
        return when (category.lowercase(Locale.getDefault())) {
            "morning" -> "아침"
            "lunch" -> "점심"
            "dinner" -> "저녁"
            "bedtime" -> "취침 전"
            else -> category.replaceFirstChar { it.uppercase() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}