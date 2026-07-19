package `in`.sumit.learningplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import `in`.sumit.learningplanner.data.local.entity.CompletionHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompletionHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: CompletionHistoryEntity)

    @Query("SELECT * FROM completion_history ORDER BY completedAt DESC")
    fun getAllHistory(): Flow<List<CompletionHistoryEntity>>

    @Query("DELETE FROM completion_history")
    suspend fun clearHistory()
}

