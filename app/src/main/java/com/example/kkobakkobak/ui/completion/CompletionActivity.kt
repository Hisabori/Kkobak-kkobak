package com.example.kkobakkobak.ui.completion

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kkobakkobak.R

class CompletionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completion)

        // Intent로부터 메시지를 전달받아 TextView에 설정합니다.
        // 전달된 메시지가 없으면 기본값 "완료!"를 사용합니다.
        val message = intent.getStringExtra("message") ?: "완료!"
        findViewById<TextView>(R.id.completion_text).text = message

        findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            finish() // '확인' 버튼을 누르면 화면 닫기
        }
    }
}