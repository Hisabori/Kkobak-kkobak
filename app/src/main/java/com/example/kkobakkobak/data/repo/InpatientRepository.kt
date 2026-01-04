package com.example.kkobakkobak.data.repo

import android.content.Context
import com.example.kkobakkobak.data.database.AppDatabase // 💡 경로 확인!
import com.example.kkobakkobak.data.database.InpatientEntity // 💡 아까 옮긴 경로로 수정!
import kotlinx.coroutines.flow.Flow

class InpatientRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val inpatientDao = database.inpatientDao()

    // 💡 Dao에 정의된 observeAll()을 불러야 함!
    fun getAllInpatients(): Flow<List<InpatientEntity>> = inpatientDao.observeAll()

    // 💡 Dao에 정의된 upsertAll()을 불러야 함!
    suspend fun insertInpatients(inpatients: List<InpatientEntity>) {
        inpatientDao.upsertAll(inpatients)
    }
}