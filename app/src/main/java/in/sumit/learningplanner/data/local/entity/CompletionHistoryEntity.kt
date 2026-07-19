package `in`.sumit.learningplanner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completion_history")
data class CompletionHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long,
    val completedAt: Long = System.currentTimeMillis(),
    val type: String = "TASK"
)

