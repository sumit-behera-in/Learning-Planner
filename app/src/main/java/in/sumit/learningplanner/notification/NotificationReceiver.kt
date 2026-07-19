package `in`.sumit.learningplanner.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import `in`.sumit.learningplanner.data.repository.TaskRepository
import `in`.sumit.learningplanner.workers.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        if (taskId == -1L) return

        when (intent.action) {
            ACTION_MARK_COMPLETE -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val task = taskRepository.getTaskById(taskId)
                    task?.subtasks?.forEach { subTask ->
                        taskRepository.updateSubTaskCompletion(subTask.id, true, taskId)
                    }
                    NotificationManagerCompat.from(context).cancel(taskId.toInt())
                }
            }
            ACTION_SNOOZE -> {
                // Schedule WorkManager to trigger after 15 mins
                val inputData = Data.Builder()
                    .putLong(ReminderScheduler.KEY_TASK_ID, taskId)
                    .build()
                    
                val snoozeWork = OneTimeWorkRequestBuilder<ReminderScheduler>()
                    .setInitialDelay(15, TimeUnit.MINUTES)
                    .setInputData(inputData)
                    .build()
                    
                WorkManager.getInstance(context).enqueue(snoozeWork)
                NotificationManagerCompat.from(context).cancel(taskId.toInt())
            }
        }
    }

    companion object {
        const val ACTION_MARK_COMPLETE = "in.sumit.learningplanner.MARK_COMPLETE"
        const val ACTION_SNOOZE = "in.sumit.learningplanner.SNOOZE"
        const val EXTRA_TASK_ID = "task_id"
    }
}

