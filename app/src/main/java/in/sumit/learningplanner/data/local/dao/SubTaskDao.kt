package `in`.sumit.learningplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import `in`.sumit.learningplanner.data.local.entity.SubTaskEntity

@Dao
interface SubTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTasks(subTasks: List<SubTaskEntity>)

    @Update
    suspend fun updateSubTask(subTask: SubTaskEntity)

    @Query("UPDATE subtasks SET isCompleted = :isCompleted WHERE id = :subTaskId")
    suspend fun updateSubTaskCompletion(subTaskId: Long, isCompleted: Boolean)
    
    @Query("SELECT * FROM subtasks WHERE taskId = :taskId")
    suspend fun getSubTasksForTask(taskId: Long): List<SubTaskEntity>
}

