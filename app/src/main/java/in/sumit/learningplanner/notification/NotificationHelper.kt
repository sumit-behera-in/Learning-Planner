package `in`.sumit.learningplanner.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import `in`.sumit.learningplanner.MainActivity
import `in`.sumit.learningplanner.domain.model.Task
import `in`.sumit.learningplanner.R

object NotificationHelper {
    const val CHANNEL_ID = "study_reminders"
    
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Study Reminders"
            val descriptionText = "Reminders for your learning tasks"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context, task: Task) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("taskId", task.id)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, task.id.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Mark complete action
        val markCompleteIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_MARK_COMPLETE
            putExtra(NotificationReceiver.EXTRA_TASK_ID, task.id)
        }
        val markCompletePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                context, 
                task.id.toInt() + 1000, 
                markCompleteIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        // Snooze action
        val snoozeIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_SNOOZE
            putExtra(NotificationReceiver.EXTRA_TASK_ID, task.id)
        }
        val snoozePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                context, 
                task.id.toInt() + 2000, 
                snoozeIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO replace with actual icon
            .setContentTitle("📚 Today's Learning Task")
            .setContentText("${task.listName}: ${task.title}")
            .setStyle(NotificationCompat.BigTextStyle().bigText(task.objective))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(0, "Mark Complete", markCompletePendingIntent)
            .addAction(0, "Snooze 15m", snoozePendingIntent)
            .addAction(0, "Open Task", pendingIntent)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(task.id.toInt(), builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}

