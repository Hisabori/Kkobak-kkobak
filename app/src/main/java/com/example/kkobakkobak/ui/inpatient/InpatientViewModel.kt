package com.example.kkobakkobak.ui.inpatient

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.kkobakkobak.data.db.AppDatabase
import com.example.kkobakkobak.data.db.InpatientEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class InpatientViewModel(application: Application) : AndroidViewModel(application) {

    // 프로젝트의 AppDatabase에 정적 getInstance가 없다면 Room으로 직접 빌드
    private val db: AppDatabase = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "kkobak.db"
    ).fallbackToDestructiveMigration().build()

    private val dao = db.inpatientDao()

    // 화면에서 관찰할 리스트
    val items: Flow<List<InpatientEntity>> = dao.observeAll()

    // 당겨서 새로고침(지금은 no-op; 추후 CSV/네트워크 로딩 넣으면 됨)
    fun load() = viewModelScope.launch {
        // TODO: 필요 시 dao.clear(); dao.upsertAll(...) 등 갱신 로직 추가
    }
}
