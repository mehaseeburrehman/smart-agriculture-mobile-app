package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM scan_records ORDER BY timestamp DESC")
    fun getAllScanRecords(): Flow<List<ScanRecordEntity>>

    @Query("SELECT * FROM scan_records WHERE id = :id LIMIT 1")
    suspend fun getScanRecordById(id: String): ScanRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScanRecord(record: ScanRecordEntity)

    @Delete
    suspend fun deleteScanRecord(record: ScanRecordEntity)

    @Query("DELETE FROM scan_records")
    suspend fun clearScanRecords()

    @Query("SELECT * FROM soil_records ORDER BY timestamp DESC")
    fun getAllSoilRecords(): Flow<List<SoilRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoilRecord(record: SoilRecordEntity)

    @Delete
    suspend fun deleteSoilRecord(record: SoilRecordEntity)

    @Query("DELETE FROM soil_records")
    suspend fun clearSoilRecords()
}
