package `in`.sumit.learningplanner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val listName: String,
    val date: Long,
    val objective: String,
    val estimatedTime: String,
    val theory: String,
    val docs: String,
    val course: String,
    val youtube: String,
    val exercise: String,
    val interviewPrep: String,
    val deliverable: String,
    val isCompleted: Boolean = false,
    val reminderTime: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

