// 수정 시작: hisabori/kkobak-kkobak/Kkobak-kkobak-29057115cdcc12e9d4b942881ac29951e9270d0a/app/src/main/java/com/example/kkobakkobak/ui/main/MainActivity.kt

package com.example.kkobakkobak.ui.main

import android.animation.Animator
import android.content.ComponentName
import android.content.Intent // 👈 import 추가
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.kkobakkobak.R
import com.example.kkobakkobak.databinding.ActivityMainBinding
import com.example.kkobakkobak.ui.inpatient.InpatientFragment
import com.example.kkobakkobak.ui.log.LogFragment
import com.example.kkobakkobak.ui.medication.MedicationFragment
import com.example.kkobakkobak.ui.mood.MoodFragment
import com.example.kkobakkobak.ui.path.PathFragment
import com.example.kkobakkobak.ui.settings.SettingsFragment
import kotlinx.coroutines.launch

import com.example.kkobakkobak.ui.main.HomeFragment
import com.example.kkobakkobak.ui.alarm.AlarmFullscreenActivity // 👈 신규 Activity import
import android.app.AlertDialog // 👈 import 추가

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private var selectedTab: Int = R.id.navigation_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 👇 [테스트] 앱이 실행되면 아이콘을 'Sad'로 변경 (필요 없으면 삭제 가능)
        changeAppIcon(isSad = true)

        // 1. 초기 상태: 바텀 네비게이션 숨김
        binding.bottomNavigation.visibility = View.GONE

        // 2. 스플래시 애니메이션 설정 및 실행
        setupSplashAnimation()

        // 3. 기타 설정
        setupBottomNavigationView()
        setupStreakUpdateFlowObserver()

        // 🔔 [수정] 테스트 알람 버튼 설정을 긴급 비상약 버튼 설정으로 변경
        setupPanicButton() // 👈 함수명 변경
    }

    // 🔔 [수정] 긴급 상황 비상약 알람 버튼으로 변경 (개인 정보 활용: 자낙스 0.25mg)
    private fun setupPanicButton() {
        binding.testAlarmButton.setOnClickListener {
            // 긴급 상황 확인 다이얼로그 추가
            AlertDialog.Builder(this)
                .setTitle("🚨 긴급 비상약 요청")
                .setMessage("자낙스 0.25mg (공황 비상약) 알람을 실행하시겠어요?") // 👈 확인 메시지 추가
                .setPositiveButton("예, 실행합니다") { _, _ ->
                    val emergencyIntent = Intent(this, AlarmFullscreenActivity::class.java).apply {
                        // 테스트 ID 999 사용 (DB에 없는 ID여도 테스트는 가능)
                        putExtra("REMINDER_ID", 999)
                        putExtra("CATEGORY", "비상약")
                        // 저장된 정보를 활용하여 자낙스 0.25mg로 메시지 설정
                        putExtra("MEDICATION_NAME", "자낙스 0.25mg (공황 비상약)") //
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(emergencyIntent)
                }
                .setNegativeButton("취소", null)
                .show()
        }
    }


    private fun setupSplashAnimation() {
        // XML에서 autoPlay=true로 설정했으므로 자동 재생됨
        // 리스너를 통해 애니메이션 종료 시점 감지
        binding.lottieSplash.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                // 애니메이션이 끝나면:
                // 1. Lottie 뷰 숨기기
                binding.lottieSplash.visibility = View.GONE
                // 2. 바텀 네비게이션 보이기
                binding.bottomNavigation.visibility = View.VISIBLE

                // 3. 홈 화면으로 이동
                replaceFragment(HomeFragment())
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun setupStreakUpdateFlowObserver() {
        lifecycleScope.launch {
            viewModel.streakUpdateEvent.collect {
                val current = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (current is LogFragment) {
                    current.updateStreakDisplay()
                }
            }
        }
    }

    private fun setupBottomNavigationView() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    selectedTab = R.id.navigation_home
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.navigation_medication -> {
                    selectedTab = R.id.navigation_medication
                    replaceFragment(MedicationFragment())
                    true
                }
                R.id.navigation_mood -> {
                    selectedTab = R.id.navigation_mood
                    replaceFragment(MoodFragment())
                    true
                }
                R.id.navigation_inpatient -> {
                    selectedTab = R.id.navigation_inpatient
                    replaceFragment(InpatientFragment())
                    true
                }
                R.id.navigation_path -> {
                    selectedTab = R.id.navigation_path
                    replaceFragment(PathFragment())
                    true
                }
                R.id.navigation_settings -> {
                    selectedTab = R.id.navigation_settings
                    replaceFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    // 아이콘 변경 로직
    private fun changeAppIcon(isSad: Boolean) {
        val packageManager = packageManager
        val angryComponent = ComponentName(this, "com.example.kkobakkobak.ui.main.MainActivityAngry")
        val sadComponent = ComponentName(this, "com.example.kkobakkobak.ui.main.MainActivitySad")

        // 1. Sad 아이콘 켜기/끄기
        packageManager.setComponentEnabledSetting(
            sadComponent,
            if (isSad) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )

        // 2. Angry 아이콘 끄기/켜기
        packageManager.setComponentEnabledSetting(
            angryComponent,
            if (isSad) PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    // Fragment 교체 헬퍼 함수
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitAllowingStateLoss()
    }
}
// 수정 끝: hisabori/kkobak-kkobak/Kkobak-kkobak-29057115cdcc12e9d4b942881ac29951e9270d0a/app/src/main/java/com/example/kkobakkobak/ui/main/MainActivity.kt