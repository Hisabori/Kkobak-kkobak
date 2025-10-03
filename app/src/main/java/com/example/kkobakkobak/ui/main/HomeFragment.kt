package com.example.kkobakkobak.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.example.kkobakkobak.databinding.FragmentHomeBinding
import com.example.kkobakkobak.ui.log.LogActivity
import com.example.kkobakkobak.ui.history.LogHistoryActivity
import com.example.kkobakkobak.ui.record.RecordActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.kkobakkobak.R
import com.example.kkobakkobak.data.database.AppDatabase
import kotlinx.coroutines.launch
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build

// 🚨 글래스모피즘 블러 효과 적용 함수 (API 31/Android 12 이상에서만 작동)
fun applyGlassmorphismBlur(view: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        view.setRenderEffect(
            RenderEffect.createBlurEffect(
                20f, // 블러 강도
                20f,
                Shader.TileMode.CLAMP // Shader 클래스 명시
            )
        )
    }
}

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🚨 글래스모피즘 블러 적용
        // HomeFragment의 주요 콘텐츠 뷰에 블러를 적용 (예: 메인 카드 뷰, 리사이클러 뷰 등)
        // FragmentHomeBinding에 정의된 메인 카드 뷰 ID가 'mainCardLayout'이라고 가정하고 적용
        // 실제 ID에 맞게 수정이 필요할 수 있어.
        // 현재는 편의상 HomeFragment의 root view에 적용해볼게.

        // 만약 특정 카드 뷰 (예: 오늘 현황을 보여주는 뷰)에만 적용하고 싶다면:
        // applyGlassmorphismBlur(binding.mainStatusCard)
        // 여기서 binding.mainStatusCard는 fragment_home.xml 내의 CardView ID라고 가정.

        // 전체 Fragment 뷰에 블러를 적용하면 화면이 이상해질 수 있으니,
        // CardView나 특정 컨테이너에만 적용하는 것을 추천해.
        // 여기서는 예시로 binding.root (Fragment의 최상위 뷰)에 적용해보자.
        // 다만, 카드 뷰에만 적용하려면 fragment_home.xml 내의 해당 뷰 ID를 확인해야 해.
        // **(XML에서 반투명 배경색을 설정했다면, 블러 적용은 필수!)**

        // [선택] 만약 리사이클러뷰의 각 아이템에 블러를 적용하려면 Adapter에서 처리해야 해.

        // ✅ 오늘 복용 현황 텍스트 업데이트
        val db = AppDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            val todayIntakes = db.medicationIntakeDao().getTodayIntakeList()
            val taken = todayIntakes.map { it.medicationName }

            val times = listOf("아침", "점심", "저녁", "취침전")
            val result = times.joinToString("\n") {
                "$it: ${if (taken.contains(it)) "복용 완료" else "미복용"}"
            }

            binding.tvTodayStatus.text = result

            // 데이터 업데이트 후, 오늘 현황 카드가 있다면 여기에 블러를 적용 (ID는 임시로 사용)
            // val statusCard = view.findViewById<View>(R.id.card_today_status)
            // if (statusCard != null) {
            //     applyGlassmorphismBlur(statusCard)
            // }
        }

        // 기존 기능들
        binding.btnAddLog.setOnClickListener {
            startActivity(Intent(requireContext(), LogActivity::class.java))
        }

        binding.btnViewHistory.setOnClickListener {
            startActivity(Intent(requireContext(), LogHistoryActivity::class.java))
        }

        binding.btnRecordMood.setOnClickListener {
            startActivity(Intent(requireContext(), RecordActivity::class.java))
        }

        binding.btnViewMoodDetails.setOnClickListener {
            val nav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
            nav?.selectedItemId = R.id.navigation_mood
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}