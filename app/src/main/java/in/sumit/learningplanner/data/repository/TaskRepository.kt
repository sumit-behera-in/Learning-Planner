package `in`.sumit.learningplanner.data.repository

import `in`.sumit.learningplanner.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun getTaskById(taskId: Long): Task?
    suspend fun updateSubTaskCompletion(subTaskId: Long, isCompleted: Boolean, taskId: Long)
    suspend fun deleteTask(taskId: Long)
    suspend fun clearAllTasks()
    suspend fun checkAndUpdateTaskCompletion(taskId: Long)
}

