package `in`.sumit.learningplanner.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import `in`.sumit.learningplanner.ui.screens.home.HomeScreen
import `in`.sumit.learningplanner.ui.screens.dashboard.DashboardScreen
import `in`.sumit.learningplanner.ui.screens.calendar.CalendarScreen
import `in`.sumit.learningplanner.ui.screens.search.SearchScreen
import `in`.sumit.learningplanner.ui.screens.settings.SettingsScreen
import `in`.sumit.learningplanner.ui.screens.detail.TaskDetailScreen

@Composable
fun NavGraph(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onNavigateToTask = { taskId ->
                navController.navigate(Screen.TaskDetail.createRoute(taskId))
            })
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
        composable(Screen.Calendar.route) {
            CalendarScreen(onNavigateToTask = { taskId ->
                navController.navigate(Screen.TaskDetail.createRoute(taskId))
            })
        }
        composable(Screen.Search.route) {
            SearchScreen(onNavigateToTask = { taskId ->
                navController.navigate(Screen.TaskDetail.createRoute(taskId))
            })
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
            TaskDetailScreen(taskId = taskId, onNavigateBack = { navController.popBackStack() })
        }
    }
}

