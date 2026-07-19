package `in`.sumit.learningplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import `in`.sumit.learningplanner.ui.components.BottomNavBar
import `in`.sumit.learningplanner.ui.navigation.NavGraph
import `in`.sumit.learningplanner.ui.theme.LearningPlannerTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle explicit intents to open a specific task
        val initialTaskId = intent.getLongExtra("taskId", -1L)
        
        setContent {
            LearningPlannerTheme {
                val navController = rememberNavController()
                
                Scaffold(
                    bottomBar = { BottomNavBar(navController) }
                ) { paddingValues ->
                    NavGraph(navController = navController, paddingValues = paddingValues)
                }
            }
        }
    }
}

