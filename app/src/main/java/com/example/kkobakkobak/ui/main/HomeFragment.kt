package com.example.kkobakkobak.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.kkobakkobak.R
import com.example.kkobakkobak.data.database.AppDatabase
import com.example.kkobakkobak.databinding.FragmentHomeBinding
import com.example.kkobakkobak.ui.history.LogHistoryActivity
import com.example.kkobakkobak.ui.log.LogActivity
import com.example.kkobakkobak.ui.record.RecordActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.Executor

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    // 알림 권한 요청 런처
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 권한 허용 시 서비스 재호출 가능
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. 🔒 화면 가리기
        applyGlassmorphismBlur(binding.root)

        // 2. 🛠️ 생체 인증 준비
        setupBiometricAuth()

        // 3. 🔔 권한 체크 (알림 + Now Bar 격상 권한)
        checkNotificationPermission()

        // 4. 🚀 자동 지문 인증 시작
        authenticateUser()

        // 5. 화면 터치 시 재시도
        binding.root.setOnClickListener {
            authenticateUser()
        }

        // ✅ 데이터 로드 & Now Bar 실행
        val db = AppDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            val todayIntakes = db.medicationIntakeDao().getTodayIntakeList()
            val takenCount = todayIntakes.size

            val statusMessage = if (takenCount > 0) {
                "오늘 ${takenCount}회 복용 완료! 🔥"
            } else {
                "오늘 약 챙겨 드셨나요? 💪"
            }
            binding.tvStreak.text = statusMessage

            // 서비스 시작
            startNowBarService(statusMessage)
        }

        // ✅ 꿀팁 & 버튼 연결
        loadDailyHealthTip()
        setupButtons()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 일반 알림 권한 확인
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun startNowBarService(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(requireContext(), MedicationNowBarService::class.java).apply {
                putExtra("status", message)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireContext().startForegroundService(intent)
            } else {
                requireContext().startService(intent)
            }
        }
    }

    private fun loadDailyHealthTip() {
        val tips = resources.getStringArray(R.array.health_tips_dataset)
        if (tips.isNotEmpty()) {
            val calendar = Calendar.getInstance()
            val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            val tipIndex = dayOfYear % tips.size
            binding.tvTodayTip.text = tips[tipIndex]
        }
    }

    private fun applyGlassmorphismBlur(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(RenderEffect.createBlurEffect(30f, 30f, Shader.TileMode.CLAMP))
        } else {
            view.alpha = 0.3f
        }
    }

    private fun removeBlur(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(null)
        } else {
            view.alpha = 1.0f
        }
    }

    private fun setupBiometricAuth() {
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    removeBlur(binding.root)
                    binding.root.setOnClickListener(null)
                    Toast.makeText(context, "환영합니다!", Toast.LENGTH_SHORT).show()
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode !in listOf(BiometricPrompt.ERROR_USER_CANCELED, BiometricPrompt.ERROR_NEGATIVE_BUTTON)) {
                        Toast.makeText(context, "인증 실패: $errString", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(context, "지문 불일치", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("잠금 해제")
            .setSubtitle("생체 정보를 인증해주세요")
            .setNegativeButtonText("취소")
            .build()
    }

    private fun authenticateUser() {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> biometricPrompt.authenticate(promptInfo)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                removeBlur(binding.root) // 지문 없으면 자동 해제
            }
            else -> {}
        }
    }

    private fun setupButtons() {
        binding.btnQuickTakeInside.setOnClickListener { startActivity(Intent(requireContext(), LogActivity::class.java)) }
        binding.btnViewHistory.setOnClickListener { startActivity(Intent(requireContext(), LogHistoryActivity::class.java)) }
        binding.btnRecordMood.setOnClickListener { startActivity(Intent(requireContext(), RecordActivity::class.java)) }
        binding.btnViewMoodDetails.setOnClickListener {
            activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.navigation_mood
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}