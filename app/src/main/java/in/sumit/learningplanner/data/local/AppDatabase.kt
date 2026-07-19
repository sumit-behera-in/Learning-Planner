package `in`.sumit.learningplanner.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import `in`.sumit.learningplanner.data.local.dao.CompletionHistoryDao
import `in`.sumit.learningplanner.data.local.dao.SubTaskDao
import `in`.sumit.learningplanner.data.local.dao.TaskDao
import `in`.sumit.learningplanner.data.local.entity.CompletionHistoryEntity
import `in`.sumit.learningplanner.data.local.entity.SubTaskEntity
import `in`.sumit.learningplanner.data.local.entity.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        SubTaskEntity::class,
        CompletionHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun subTaskDao(): SubTaskDao
    abstract fun completionHistoryDao(): CompletionHistoryDao
    
    companion object {
        const val DATABASE_NAME = "learning_planner_db"
    }
}

