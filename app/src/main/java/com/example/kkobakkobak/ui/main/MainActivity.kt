package com.example.kkobakkobak.ui.main

import android.content.ComponentName
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
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

// 👇 HomeFragment 경로가 맞는지 확인하세요
import com.example.kkobakkobak.ui.main.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appTitleTypewriter: TextView
    private val viewModel: MainViewModel by viewModels()

    private var selectedTab: Int = R.id.navigation_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 👇 [테스트] 앱이 실행되면 아이콘을 'Sad'로 변경 (테스트 후 삭제 가능)
        changeAppIcon(isSad = true)

        appTitleTypewriter = binding.appTitleTypewriter

        playTypewriterEffectAndShowMainContent()
        setupBottomNavigationView()
        setupStreakUpdateFlowObserver()
    }

    // 👇 아이콘 변경 로직 (PackageManager 사용)
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

            // 타이핑 효과 끝난 후 홈 화면으로 이동
            replaceFragment(HomeFragment())
        }
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

    // 👇 [수정됨] IllegalStateException 방지를 위해 commitAllowingStateLoss 사용
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitAllowingStateLoss()
    }
}