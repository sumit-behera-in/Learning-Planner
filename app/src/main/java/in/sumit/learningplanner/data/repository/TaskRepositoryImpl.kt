package `in`.sumit.learningplanner.data.repository

import `in`.sumit.learningplanner.data.local.dao.CompletionHistoryDao
import `in`.sumit.learningplanner.data.local.dao.SubTaskDao
import `in`.sumit.learningplanner.data.local.dao.TaskDao
import `in`.sumit.learningplanner.data.local.entity.CompletionHistoryEntity
import `in`.sumit.learningplanner.data.local.relations.TaskWithSubTasks
import `in`.sumit.learningplanner.domain.model.SubTask
import `in`.sumit.learningplanner.domain.model.SubTaskType
import `in`.sumit.learningplanner.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val subTaskDao: SubTaskDao,
    private val completionHistoryDao: CompletionHistoryDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)?.toDomainModel()
    }

    override suspend fun updateSubTaskCompletion(subTaskId: Long, isCompleted: Boolean, taskId: Long) {
        subTaskDao.updateSubTaskCompletion(subTaskId, isCompleted)
        checkAndUpdateTaskCompletion(taskId)
    }

    override suspend fun deleteTask(taskId: Long) {
        taskDao.deleteTask(taskId)
    }

    override suspend fun clearAllTasks() {
        taskDao.clearAllTasks()
    }

    override suspend fun checkAndUpdateTaskCompletion(taskId: Long) {
        val subTasks = subTaskDao.getSubTasksForTask(taskId)
        val allCompleted = subTasks.isNotEmpty() && subTasks.all { it.isCompleted }
        
        taskDao.updateTaskCompletion(taskId, allCompleted)
        
        if (allCompleted) {
            completionHistoryDao.insertHistory(
                CompletionHistoryEntity(taskId = taskId, type = "TASK")
            )
        }
    }

    private fun TaskWithSubTasks.toDomainModel(): Task {
        return Task(
            id = task.id,
            title = task.title,
            listName = task.listName,
            date = task.date,
            objective = task.objective,
            estimatedTime = task.estimatedTime,
            theory = task.theory,
            docs = task.docs,
            course = task.course,
            youtube = task.youtube,
            exercise = task.exercise,
            interviewPrep = task.interviewPrep,
            deliverable = task.deliverable,
            isCompleted = task.isCompleted,
            reminderTime = task.reminderTime,
            createdAt = task.createdAt,
            subtasks = subTasks.map {
                SubTask(
                    id = it.id,
                    taskId = it.taskId,
                    type = try { SubTaskType.valueOf(it.type) } catch(e: Exception) { SubTaskType.THEORY },
                    content = it.content,
                    isCompleted = it.isCompleted
                )
            }
        )
    }
}

