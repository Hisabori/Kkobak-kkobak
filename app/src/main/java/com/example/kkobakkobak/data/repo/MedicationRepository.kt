package com.example.kkobakkobak.data.repo

import com.example.kkobakkobak.data.dao.MedicationLogDao
import com.example.kkobakkobak.data.model.MedicationLog
import com.example.kkobakkobak.network.RetrofitClient
import com.example.kkobakkobak.data.pref.AuthDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MedicationRepository(
    private val localMedicationDao: MedicationLogDao,
    private val authDataStore: AuthDataStore
) {
    private val apiService = RetrofitClient.apiService

    suspend fun saveMedicationLog(log: MedicationLog) = withContext(Dispatchers.IO) {
        val token = authDataStore.loadAuthToken() ?: throw Exception("인증 토큰이 필요합니다.")
        val response = apiService.saveMedicationLog("Bearer $token", log)

        if (response.isSuccessful && response.body() != null) {
            localMedicationDao.insert(response.body()!!)
        }
    }

    suspend fun syncAllMedicationLogs() = withContext(Dispatchers.IO) {
        val token = authDataStore.loadAuthToken() ?: return@withContext
        val response = apiService.getMedicationLogs("Bearer $token")

        if (response.isSuccessful && response.body() != null) {
            localMedicationDao.deleteAllAndInsertAll(response.body()!!)
        }
    }

    fun getAllMedicationLogs(): Flow<List<MedicationLog>> = localMedicationDao.getAll()
}