package `in`.sumit.learningplanner.ui.screens.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateToTask: (Long) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    val selectedDateState = rememberSaveable { mutableStateOf(getStartOfDay(System.currentTimeMillis())) }
    val weekDates = buildWeekDates(selectedDateState.value)
    val selectedDayTasks = tasks.filter { isSameDay(it.date, selectedDateState.value) }.sortedBy { it.date }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Calendar") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Select a day",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(weekDates) { dateMillis ->
                    val isSelected = isSameDay(dateMillis, selectedDateState.value)
                    Card(
                        modifier = Modifier.clickable { selectedDateState.value = getStartOfDay(dateMillis) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = formatDayLabel(dateMillis),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = formatDayNumber(dateMillis),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Text(
                text = "Tasks for ${formatDate(selectedDateState.value)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (selectedDayTasks.isEmpty()) {
                Text(
                    text = "No tasks scheduled for this day.",
                    modifier = Modifier.padding(top = 12.dp)
                )
            } else {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedDayTasks.forEach { task ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToTask(task.id) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(task.title, fontWeight = FontWeight.SemiBold)
                                if (task.objective.isNotBlank()) {
                                    Text(task.objective, style = MaterialTheme.typography.bodyMedium)
                                }
                                Text(
                                    text = task.listName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun buildWeekDates(anchorMillis: Long): List<Long> {
    val startDate = Instant.ofEpochMilli(anchorMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    val dayOfWeek = startDate.dayOfWeek.value
    val startOfWeek = startDate.minusDays((dayOfWeek - 1).toLong())

    return (0 until 7).map { offset ->
        startOfWeek.plusDays(offset.toLong()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}

private fun getStartOfDay(timeInMillis: Long): Long {
    return Instant.ofEpochMilli(timeInMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

private fun isSameDay(firstMillis: Long, secondMillis: Long): Boolean {
    val firstDate = Instant.ofEpochMilli(firstMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    val secondDate = Instant.ofEpochMilli(secondMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    return firstDate == secondDate
}

private fun formatDayLabel(timeInMillis: Long): String {
    return Instant.ofEpochMilli(timeInMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault()))
}

private fun formatDayNumber(timeInMillis: Long): String {
    return Instant.ofEpochMilli(timeInMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("dd", Locale.getDefault()))
}

private fun formatDate(timeInMillis: Long): String {
    return Instant.ofEpochMilli(timeInMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("EEE, d MMM", Locale.getDefault()))
}

