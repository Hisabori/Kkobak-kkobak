// InpatientRepository.kt
package com.example.kkobakkobak.data.repo

import android.content.Context
import androidx.room.withTransaction   // ⬅️ 추가
import com.example.kkobakkobak.R
import com.example.kkobakkobak.data.db.AppDatabase
import com.example.kkobakkobak.data.db.InpatientDao
import com.example.kkobakkobak.data.db.InpatientEntity
import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import java.nio.charset.Charset
import java.time.LocalDate

class InpatientRepository(
    private val dao: InpatientDao,
    private val db: AppDatabase
) {
    fun observeAll(): Flow<List<InpatientEntity>> = dao.observeAll()
    suspend fun getMin() = dao.getMin()
    suspend fun getMax() = dao.getMax()

    /** 최초 1회 CSV를 DB에 시드 */
    suspend fun ensureSeededFromCsv(ctx: Context) {
        if (dao.count() > 0) return
        val input = ctx.resources.openRawResource(R.raw.inpatient_2020)
        val items = parseCsv(input)

        // ✅ 트랜잭션을 suspend 블록으로
        db.withTransaction {
            dao.upsertAll(items)
        }
    }

    private fun parseCsv(input: InputStream): List<InpatientEntity> {
        val list = mutableListOf<InpatientEntity>()
        input.bufferedReader(Charset.forName("UTF-8")).useLines { lines ->
            lines.forEach { raw ->
                val line = raw.trim().removePrefix("\uFEFF")
                if (line.isEmpty()) return@forEach
                if (line.startsWith("일자") || line.startsWith("총계")) return@forEach

                val parts = line.split(",")
                if (parts.size < 3) return@forEach

                val date = runCatching { LocalDate.parse(parts[0].trim()) }.getOrNull() ?: return@forEach
                val weekday = parts[1].trim()
                val count = parts[2].trim().toIntOrNull() ?: return@forEach

                list += InpatientEntity(date = date, weekday = weekday, count = count)
            }
        }
        return list
    }
}
