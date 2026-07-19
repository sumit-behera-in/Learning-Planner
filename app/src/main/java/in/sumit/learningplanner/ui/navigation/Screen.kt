package `in`.sumit.learningplanner.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Home : Screen("home", "Tasks", Icons.Filled.Checklist)
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Dashboard)
    object Calendar : Screen("calendar", "Calendar", Icons.Filled.CalendarMonth)
    object Search : Screen("search", "Search", Icons.Filled.Search)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
    object TaskDetail : Screen("task_detail/{taskId}", "Task Detail", null) {
        fun createRoute(taskId: Long) = "task_detail/$taskId"
    }
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Dashboard,
    Screen.Calendar,
    Screen.Search,
    Screen.Settings
)

