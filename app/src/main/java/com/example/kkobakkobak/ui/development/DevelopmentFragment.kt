package com.example.kkobakkobak.ui.development

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kkobakkobak.R

class DevelopmentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // "계발중" UI를 위한 레이아웃을 인플레이트합니다.
        return inflater.inflate(R.layout.fragment_development, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 여기에 "계발중" 관련 UI 요소 초기화 및 로직을 추가할 수 있습니다.
    }
}
