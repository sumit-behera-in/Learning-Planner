package `in`.sumit.learningplanner.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dashboard") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Total Tasks: ${stats.totalTaskCount}", style = MaterialTheme.typography.titleLarge)
            Text("Completed: ${stats.completedTaskCount}", style = MaterialTheme.typography.titleLarge)
            Text("Overdue: ${stats.overdueTaskCount}", style = MaterialTheme.typography.titleLarge)
            Text("Study Streak: ${stats.studyStreakDays} days", style = MaterialTheme.typography.titleLarge)
            
            // TODO: Implement Canvas-based charts for weekly/monthly progress here
        }
    }
}

