package `in`.sumit.learningplanner.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import `in`.sumit.learningplanner.data.repository.TaskRepository
import `in`.sumit.learningplanner.notification.NotificationHelper

@HiltWorker
class ReminderScheduler @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskRepository: TaskRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(KEY_TASK_ID, -1L)
        if (taskId == -1L) return Result.failure()

        val task = taskRepository.getTaskById(taskId) ?: return Result.failure()
        
        if (!task.isCompleted) {
            NotificationHelper.showNotification(context, task)
        }

        return Result.success()
    }

    companion object {
        const val KEY_TASK_ID = "task_id"
    }
}

