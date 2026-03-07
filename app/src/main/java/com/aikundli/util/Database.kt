package com.aikundli.util

import android.content.Context
import androidx.room.*
import com.aikundli.model.SavedReport
import kotlinx.coroutines.flow.Flow

// ── DAO ───────────────────────────────────────────────────────────────────

@Dao
interface SavedReportDao {
    @Query("SELECT * FROM saved_reports ORDER BY createdAt DESC")
    fun getAllReports(): Flow<List<SavedReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: SavedReport)

    @Delete
    suspend fun deleteReport(report: SavedReport)

    @Query("DELETE FROM saved_reports")
    suspend fun clearAll()
}

// ── Database ──────────────────────────────────────────────────────────────

@Database(entities = [SavedReport::class], version = 1, exportSchema = false)
abstract class KundliDatabase : RoomDatabase() {
    abstract fun savedReportDao(): SavedReportDao

    companion object {
        @Volatile private var INSTANCE: KundliDatabase? = null

        fun getInstance(context: Context): KundliDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    KundliDatabase::class.java,
                    "kundli_database"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}
