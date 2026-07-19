package `in`.sumit.learningplanner.domain.model

data class DashboardStats(
    val todayTaskCount: Int = 0,
    val overdueTaskCount: Int = 0,
    val completedTaskCount: Int = 0,
    val upcomingTaskCount: Int = 0,
    val totalTaskCount: Int = 0,
    val studyStreakDays: Int = 0,
    val weeklyCompletedCount: Int = 0,
    val weeklyTotalCount: Int = 0,
    val monthlyCompletedCount: Int = 0,
    val monthlyTotalCount: Int = 0
)

