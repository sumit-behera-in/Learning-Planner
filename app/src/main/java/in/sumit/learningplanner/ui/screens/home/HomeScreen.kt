package `in`.sumit.learningplanner.ui.screens.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.sumit.learningplanner.domain.model.Task
import `in`.sumit.learningplanner.ui.screens.home.components.TaskCard
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTask: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tasks by viewModel.tasks.collectAsState()
    val taskSections = buildTaskSections(tasks)

    val csvPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> uri?.let { viewModel.importCsv(it) } }
    )

    LaunchedEffect(Unit) {
        viewModel.importEvents.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Learning Planner") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                csvPickerLauncher.launch(arrayOf("text/csv", "text/comma-separated-values", "*/*"))
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Tasks")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 16.dp,
                bottom = paddingValues.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(taskSections, key = { it.title }) { section ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    section.tasks.forEach { task ->
                        TaskCard(
                            task = task,
                            completionLabel = section.title.takeIf { it == "Completed" }?.let {
                                task.completedAt?.let { formatCompletionLabel(it) }
                            },
                            onTaskComplete = { isChecked -> viewModel.completeTask(task.id, isChecked) },
                            onSubTaskComplete = { subTaskId, isChecked ->
                                viewModel.updateSubTaskCompletion(subTaskId, isChecked, task.id)
                            },
                            onClick = { onNavigateToTask(task.id) }
                        )
                    }
                }
            }
        }
    }
}

private data class TaskSection(val title: String, val tasks: List<Task>)

private fun buildTaskSections(tasks: List<Task>): List<TaskSection> {
    val activeTasks = tasks.filterNot { it.isCompleted }
        .sortedWith(compareBy<Task> { toLocalDate(it.date) }.thenBy { it.date }.thenBy { it.createdAt })

    val groupedTasks = linkedMapOf<LocalDate, MutableList<Task>>()
    activeTasks.forEach { task ->
        val localDate = toLocalDate(task.date)
        groupedTasks.getOrPut(localDate) { mutableListOf() }.add(task)
    }

    val sections = groupedTasks.entries.map { (date, items) ->
        TaskSection(
            title = getSectionTitle(date),
            tasks = items.sortedWith(compareBy<Task> { it.date }.thenBy { it.createdAt })
        )
    }.toMutableList()

    val completedTasks = tasks.filter { it.isCompleted }
        .sortedWith(compareByDescending<Task> { it.completedAt ?: it.createdAt })

    if (completedTasks.isNotEmpty()) {
        sections.add(TaskSection(title = "Completed", tasks = completedTasks))
    }

    return sections
}

private fun getSectionTitle(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> "Today"
        today.plusDays(1) -> "Tomorrow"
        else -> date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()))
    }
}

private fun toLocalDate(timestamp: Long): LocalDate {
    return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
}

private fun formatCompletionLabel(timestamp: Long): String {
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.getDefault()))
}

