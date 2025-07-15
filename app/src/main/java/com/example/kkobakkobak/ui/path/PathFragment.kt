package com.example.kkobakkobak.ui.path

//cardview
import androidx.cardview.widget.CardView
import android.graphics.Color


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kkobakkobak.databinding.FragmentPathBinding

data class ScheduleItem(
    val day: String,
    val time: String,
    val content: String
)

class PathFragment : Fragment() {

    private var _binding: FragmentPathBinding? = null
    private val binding get() = _binding!!

    // 🔄 mutableList로 변경해서 동적으로 추가 가능하게 함
    private val scheduleList = mutableListOf(
        ScheduleItem("월요일", "09:00", "정신과 진료"),
        ScheduleItem("화요일", "14:00", "DBT 프로그램"),
        ScheduleItem("수요일", "11:30", "산책/야외활동"),
        ScheduleItem("금요일", "15:00", "가족상담")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPathBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showSchedule()

        binding.btnAddSchedule.setOnClickListener {
            showAddScheduleDialog()
        }
    }

    private fun showAddScheduleDialog() {
        val dialogLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val dayInput = EditText(requireContext()).apply { hint = "요일 (예: 월요일)" }
        val timeInput = EditText(requireContext()).apply { hint = "시간 (예: 14:00)" }
        val contentInput = EditText(requireContext()).apply { hint = "내용 (예: DBT 프로그램)" }

        dialogLayout.addView(dayInput)
        dialogLayout.addView(timeInput)
        dialogLayout.addView(contentInput)

        AlertDialog.Builder(requireContext())
            .setTitle("시간표 항목 추가")
            .setView(dialogLayout)
            .setPositiveButton("추가") { _, _ ->
                val day = dayInput.text.toString()
                val time = timeInput.text.toString()
                val content = contentInput.text.toString()

                if (day.isNotBlank() && time.isNotBlank() && content.isNotBlank()) {
                    val newItem = ScheduleItem(day, time, content)
                    scheduleList.add(newItem)
                    showSchedule()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showSchedule() {
        val container = binding.timetableList
        container.removeAllViews()

        for (item in scheduleList.sortedBy { it.day + it.time }) {
            val card = CardView(requireContext()).apply {
                radius = 16f
                cardElevation = 6f
                setCardBackgroundColor(Color.parseColor("#ECECFF"))
                useCompatPadding = true
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16, 0, 0)
                }
            }

            val content = TextView(requireContext()).apply {
                text = "📅 ${item.day}  🕒 ${item.time}\n📌 ${item.content}"
                setPadding(24, 24, 24, 24)
                textSize = 16f
                setTextColor(Color.DKGRAY)
            }

            card.addView(content)
            container.addView(card)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
