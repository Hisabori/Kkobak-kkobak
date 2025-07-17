package com.example.kkobakkobak.ui.completion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kkobakkobak.R // 리소스 R 클래스

class CompletionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 완료 화면 레이아웃을 인플레이트합니다.
        return inflater.inflate(R.layout.fragment_completion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 여기에 완료 화면에 필요한 UI 초기화 및 로직을 추가합니다.
    }
}
