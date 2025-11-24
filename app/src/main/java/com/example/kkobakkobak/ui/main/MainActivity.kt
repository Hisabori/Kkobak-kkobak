package com.example.kkobakkobak.ui.main

import android.content.ComponentName
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels // 👈 ViewModel 사용을 위해 추가
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// HomeFragment import가 필요하다면 추가하세요:
import com.example.kkobakkobak.ui.main.HomeFragment // 👈 HomeFragment의 실제 경로로 수정

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appTitleTypewriter: TextView
    private val viewModel: MainViewModel by viewModels() // 👈 ViewModel 초기화

    private var selectedTab: Int = R.id.navigation_home

    override fun onCreate(savedInstanceState: Bundle?) { // 👈 onCreate 함수 복구
        super.onCreate(savedInstanceState)

        // enableEdgeToEdge() // (선택) 에지 투 에지

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 앱 아이콘 초기화
        resetAppIcon()

        appTitleTypewriter = binding.appTitleTypewriter

        playTypewriterEffectAndShowMainContent()
        setupBottomNavigationView()
        setupStreakUpdateFlowObserver() // 👈 SharedFlow 관찰 함수 호출 (LocalBroadcastManager 대체)
    }

    private fun resetAppIcon() {
        val packageManager = packageManager
        val packageName = packageName

        val defaultComponent = ComponentName(packageName, "com.example.kkobakkobak.ui.main.MainActivity")
        val angryComponent = ComponentName(packageName, "com.example.kkobakkobak.ui.main.MainActivityAngry")

        packageManager.setComponentEnabledSetting(
            defaultComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )
        packageManager.setComponentEnabledSetting(
            angryComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
    }

    private fun playTypewriterEffectAndShowMainContent() {
        val fullText = "꾸준함이 빛나는 공간, 꾸박꾸박"
        appTitleTypewriter.text = ""
        appTitleTypewriter.visibility = View.VISIBLE

        val customTypeface: Typeface? = ResourcesCompat.getFont(this, R.font.kkobakkobak)
        customTypeface?.let { appTitleTypewriter.typeface = it }

        lifecycleScope.launch {
            for (i in fullText.indices) {
                appTitleTypewriter.text = fullText.substring(0, i + 1)
                delay(200)
            }
            delay(1000)
            appTitleTypewriter.visibility = View.GONE

            // HomeFragment로 교체
            replaceFragment(HomeFragment())
        }
    }

    // 👈 SharedFlow를 관찰하는 함수 추가 (LocalBroadcastManager 대체)
    private fun setupStreakUpdateFlowObserver() {
        // Activity 생명주기에 맞춰 Flow 관찰 시작
        lifecycleScope.launch {
            viewModel.streakUpdateEvent.collect {
                val current = supportFragmentManager.findFragmentById(R.id.fragment_container)
                // LogFragment가 화면에 있다면 updateStreakDisplay() 호출
                // LogFragment가 'ui/log' 패키지에 있음을 가정
                if (current is LogFragment) {
                    current.updateStreakDisplay() // LogFragment에 이 메서드가 있다고 가정
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

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    /* 👈 이전의 setupStreakUpdateReceiver 함수는 제거되었습니다. */
}
