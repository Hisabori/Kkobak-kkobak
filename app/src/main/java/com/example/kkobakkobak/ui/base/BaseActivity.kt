package com.example.kkobakkobak.ui.base

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.kkobakkobak.R
import com.google.android.material.appbar.MaterialToolbar

/**
 * 툴바 설정과 같은 공통 기능을 담는 BaseActivity 입니다.
 * 다른 Activity들은 AppCompatActivity 대신 이 BaseActivity를 상속받습니다.
 */
abstract class BaseActivity : AppCompatActivity() {

    /**
     * 툴바를 설정하는 공통 함수
     * @param toolbar 레이아웃에 포함된 툴바
     * @param title 툴바에 표시될 제목
     * @param showBackButton 뒤로가기 버튼 표시 여부
     */
    protected fun setupToolbar(toolbar: MaterialToolbar, title: String, showBackButton: Boolean) {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            this.title = title
            setDisplayHomeAsUpEnabled(showBackButton)
            if (showBackButton) {
                setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            }
        }
    }

    // 뒤로가기 버튼 클릭 이벤트를 공통으로 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish() // 현재 Activity 종료
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
