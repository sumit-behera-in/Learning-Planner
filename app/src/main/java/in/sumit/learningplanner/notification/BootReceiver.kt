package `in`.sumit.learningplanner.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import `in`.sumit.learningplanner.data.repository.TaskRepository
import `in`.sumit.learningplanner.workers.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val tasks = taskRepository.getAllTasks().first()
                val currentTime = System.currentTimeMillis()
                
                tasks.filter { !it.isCompleted && (it.reminderTime ?: 0L) > currentTime }.forEach { task ->
                    val delay = task.reminderTime!! - currentTime
                    
                    val inputData = Data.Builder()
                        .putLong(ReminderScheduler.KEY_TASK_ID, task.id)
                        .build()
                        
                    val workRequest = OneTimeWorkRequestBuilder<ReminderScheduler>()
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .build()
                        
                    WorkManager.getInstance(context).enqueue(workRequest)
                }
            }
        }
    }
}

