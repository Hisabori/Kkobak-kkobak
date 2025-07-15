package com.example.kkobakkobak.ui.mood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kkobakkobak.R // 리소스 R 클래스

class MoodFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 이 프래그먼트의 레이아웃을 인플레이트합니다.
        // 만약 fragment_mood.xml 레이아웃 파일이 있다면 R.layout.fragment_mood로 변경해주세요.
        return inflater.inflate(R.layout.fragment_development, container, false) // 임시로 development 프래그먼트 레이아웃 사용
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 여기에 기분 기록/관리 화면에 필요한 UI 초기화 및 로직을 추가합니다.
    }
}
