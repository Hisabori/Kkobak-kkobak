// 수정 시작: hisabori/kkobak-kkobak/Kkobak-kkobak-29057115cdcc12e9d4b942881ac29951e9270d0a/app/src/main/java/com/example/kkobakkobak/ui/path/PathFragment.kt

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
import java.util.Calendar // 👈 import 추가
import android.content.Intent // 👈 import 추가
import android.net.Uri // 👈 import 추가
import android.widget.Button // 👈 import 추가

// 수정 시작: ScheduleItem에 주소 필드 추가
data class ScheduleItem(
    val day: String,
    val time: String,
    val content: String,
    val address: String = "" // 👈 주소 필드 추가 (길찾기용)
)
// 수정 끝: ScheduleItem에 주소 필드 추가

class PathFragment : Fragment() {

    private var _binding: FragmentPathBinding? = null
    private val binding get() = _binding!!

    // 🔄 mutableList로 변경해서 동적으로 추가 가능하게 함
    private val scheduleList = mutableListOf(
        ScheduleItem("월요일", "09:00", "정신과 외래", "서울 노원구 한글비석로 149"), // 👈 주소 예시 추가 (을지병원 근처)
        ScheduleItem("화요일", "14:00", "DBT 프로그램", "서울 노원구 한글비석로 149"),
        ScheduleItem("수요일", "11:30", "산책/야외활동", ""), // 주소 없는 경우 예시
        ScheduleItem("금요일", "15:00", "가족상담", "서울 노원구 한글비석로 149")
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
        val addressInput = EditText(requireContext()).apply { hint = "주소 (길찾기용, 선택)" } // 👈 주소 입력 필드 추가

        dialogLayout.addView(dayInput)
        dialogLayout.addView(timeInput)
        dialogLayout.addView(contentInput)
        dialogLayout.addView(addressInput) // 👈 주소 입력 필드 추가

        AlertDialog.Builder(requireContext())
            .setTitle("시간표 항목 추가")
            .setView(dialogLayout)
            .setPositiveButton("추가") { _, _ ->
                val day = dayInput.text.toString()
                val time = timeInput.text.toString()
                val content = contentInput.text.toString()
                val address = addressInput.text.toString() // 👈 주소 가져오기

                if (day.isNotBlank() && time.isNotBlank() && content.isNotBlank()) {
                    // 주소는 비어있어도 추가 가능하게 변경
                    val newItem = ScheduleItem(day, time, content, address) // 주소 포함해서 저장
                    scheduleList.add(newItem)
                    showSchedule()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    // 수정 시작: 오늘 일정 하이라이트 및 길찾기 버튼 추가
    private fun showSchedule() {
        val container = binding.timetableList
        container.removeAllViews()

        val today = getTodayDayOfWeek() // 오늘 요일 가져오기 (예: "월요일")

        for (item in scheduleList.sortedBy { it.day + it.time }) {
            // 1. 오늘 일정인지 확인
            val isToday = item.day == today

            val card = CardView(requireContext()).apply {
                radius = 16f
                cardElevation = 6f
                // 오늘 일정은 배경색을 다르게 하이라이트
                setCardBackgroundColor(if (isToday) Color.parseColor("#FFF3CD") else Color.parseColor("#ECECFF")) // 👈 오늘 일정 하이라이트
                useCompatPadding = true
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16, 0, 0)
                }
            }

            val innerLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(24, 24, 24, 24)
            }

            val content = TextView(requireContext()).apply {
                text = "📅 ${item.day}  🕒 ${item.time}\n📌 ${item.content}"
                textSize = 16f
                setTextColor(if (isToday) Color.parseColor("#E65100") else Color.DKGRAY) // 👈 오늘 일정 텍스트 색상 변경
            }

            innerLayout.addView(content)

            // 2. 주소가 있으면 길찾기 버튼 추가
            if (item.address.isNotBlank()) {
                val findPathButton = Button(requireContext()).apply {
                    text = "길찾기 (맵 앱 실행)" // 👈 카카오맵 연동 강조 (범용 맵 인텐트)
                    textSize = 14f
                    setBackgroundColor(Color.parseColor("#FFEB3B")) // 카카오 컬러와 유사하게 설정
                    setTextColor(Color.BLACK)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 16
                    }

                    setOnClickListener {
                        // 3. 길찾기 Intent 실행 (지리적 쿼리 Intent: 카카오맵/네이버맵 등이 설치되어 있으면 자동으로 연결됨)
                        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${item.address}"))
                        if (mapIntent.resolveActivity(requireContext().packageManager) != null) {
                            startActivity(mapIntent)
                        } else {
                            // 맵 앱이 없을 경우의 처리 (예: 토스트 메시지)
                        }
                    }
                }
                innerLayout.addView(findPathButton)
            }

            card.addView(innerLayout) // innerLayout을 카드에 추가
            container.addView(card)
        }
    }

    private fun getTodayDayOfWeek(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "일요일"
            Calendar.MONDAY -> "월요일"
            Calendar.TUESDAY -> "화요일"
            Calendar.WEDNESDAY -> "수요일"
            Calendar.THURSDAY -> "목요일"
            Calendar.FRIDAY -> "금요일"
            Calendar.SATURDAY -> "토요일"
            else -> ""
        }
    }
    // 수정 끝: 오늘 일정 하이라이트 및 길찾기 버튼 추가


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
// 수정 끝: hisabori/kkobak-kkobak/Kkobak-kkobak-29057115cdcc12e9d4b942881ac29951e9270d0a/app/src/main/java/com/example/kkobakkobak/ui/path/PathFragment.kt