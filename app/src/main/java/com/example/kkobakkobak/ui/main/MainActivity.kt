package com.example.kkobakkobak.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.kkobakkobak.R
import com.example.kkobakkobak.alarm.AlarmScheduler
import com.example.kkobakkobak.databinding.ActivityMainBinding
import com.example.kkobakkobak.ui.completion.CompletionFragment
import com.example.kkobakkobak.ui.history.LogHistoryFragment
import com.example.kkobakkobak.ui.log.LogFragment
import com.example.kkobakkobak.ui.record.RecordFragment
import com.example.kkobakkobak.ui.medication.MedicationFragment
import com.example.kkobakkobak.ui.mood.MoodFragment
import com.example.kkobakkobak.ui.path.PathFragment
import com.example.kkobakkobak.ui.settings.SettingsFragment


import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appTitleTypewriter: TextView

    private var selectedTab: Int = R.id.navigation_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appTitleTypewriter = binding.appTitleTypewriter

        playTypewriterEffectAndShowMainContent()

        setupBottomNavigationView()
        setupStreakUpdateReceiver()
        // Removed scheduleAlarms() call, as medication alarms are managed by MedicationFragment
    }

    private fun playTypewriterEffectAndShowMainContent() {
        val fullText = "꾸박꾸박"
        appTitleTypewriter.text = ""
        appTitleTypewriter.visibility = View.VISIBLE

        val customTypeface: Typeface? = ResourcesCompat.getFont(this, R.font.kkobakkobak)

        customTypeface?.let {
            appTitleTypewriter.typeface = it
        }

        CoroutineScope(Dispatchers.Main).launch {
            for (i in fullText.indices) {
                appTitleTypewriter.text = fullText.substring(0, i + 1)
                delay(200)
            }

            delay(1000)

            appTitleTypewriter.visibility = View.GONE
            replaceFragment(HomeFragment())
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

    private fun setupStreakUpdateReceiver() {
        val streakUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "com.example.kkobakkobak.ACTION_UPDATE_STREAK") {
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                    if (currentFragment is LogFragment) {
                        currentFragment.updateStreakDisplay()
                    }
                }
            }
        }
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(streakUpdateReceiver, IntentFilter("com.example.kkobakkobak.ACTION_UPDATE_STREAK"))
    }

    // Removed scheduleAlarms() function from here
}