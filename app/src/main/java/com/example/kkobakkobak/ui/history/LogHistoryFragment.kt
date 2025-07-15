package com.example.kkobakkobak.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kkobakkobak.R // 리소스 R 클래스

class LogHistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 이 프래그먼트의 레이아웃을 인플레이트합니다.
        // 만약 activity_log_history.xml 레이아웃 파일이 있다면 R.layout.activity_log_history로 변경해주세요.
        return inflater.inflate(R.layout.activity_log_history, container, false) // 임시로 activity_log_history 레이아웃 사용
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 여기에 기록 내역 화면에 필요한 UI 초기화 및 로직을 추가합니다.
    }
}
