package com.example.kkobakkobak.ui.record

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kkobakkobak.databinding.ActivityRecordBinding // 이 바인딩 클래스가 있다고 가정할게

class RecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 여기는 이제 RecordActivity 화면이 시작될 때 필요한 초기화나 UI 로직을 넣을 수 있는 곳이야.
        // 예를 들어, 화면에 "기록을 남겨주세요!" 같은 메시지를 띄우거나
        // 다른 기록 관련 UI 컴포넌트를 초기화할 수 있지.
        // binding.someTextView.text = "이곳은 기록 화면입니다!" // 예시로 텍스트뷰가 있다면 이렇게 쓸 수 있어.
    }
}