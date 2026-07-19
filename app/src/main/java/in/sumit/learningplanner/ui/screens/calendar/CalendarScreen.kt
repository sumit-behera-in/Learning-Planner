package `in`.sumit.learningplanner.ui.screens.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import `in`.sumit.learningplanner.domain.model.Task
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
    val visibleMonthState = rememberSaveable { mutableStateOf(startOfMonth(selectedDateState.value)) }
    val selectedDayTasks = tasks.filter { isSameDay(it.date, selectedDateState.value) }.sortedBy { it.date }
    val monthDays = buildMonthDays(visibleMonthState.value, tasks)

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                IconButton(onClick = { visibleMonthState.value = previousMonth(visibleMonthState.value) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous month")
                }
                Text(
                    text = formatMonthHeader(visibleMonthState.value),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = { visibleMonthState.value = nextMonth(visibleMonthState.value) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next month")
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { dayLabel ->
                    Text(dayLabel, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(monthDays) { day ->
                    val isSelected = isSameDay(day.dateMillis, selectedDateState.value)
                    Card(
                        modifier = Modifier.clickable(enabled = day.isCurrentMonth) {
                            selectedDateState.value = day.dateMillis
                            visibleMonthState.value = startOfMonth(day.dateMillis)
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                day.hasTasks -> MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.12f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = day.dayNumber,
                                style = MaterialTheme.typography.bodyMedium,
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

private data class MonthDay(
    val dateMillis: Long,
    val dayNumber: String,
    val isCurrentMonth: Boolean,
    val hasTasks: Boolean
)

private fun buildMonthDays(anchorMillis: Long, tasks: List<Task>): List<MonthDay> {
    val firstDayOfMonth = Instant.ofEpochMilli(anchorMillis).atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
    val startOfGrid = firstDayOfMonth.minusDays((firstDayOfWeek % 7).toLong())
    val currentMonth = firstDayOfMonth.monthValue

    return (0 until 42).map { offset ->
        val date = startOfGrid.plusDays(offset.toLong())
        val dateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        MonthDay(
            dateMillis = dateMillis,
            dayNumber = date.dayOfMonth.toString(),
            isCurrentMonth = date.monthValue == currentMonth,
            hasTasks = tasks.any { isSameDay(it.date, dateMillis) }
        )
    }
}

private fun startOfMonth(timeInMillis: Long): Long {
    return Instant.ofEpochMilli(timeInMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .withDayOfMonth(1)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

private fun previousMonth(timeInMillis: Long): Long {
    return Instant.ofEpochMilli(timeInMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .minusMonths(1)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

private fun nextMonth(timeInMillis: Long): Long {
    return Instant.ofEpochMilli(timeInMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .plusMonths(1)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
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

private fun formatMonthHeader(timeInMillis: Long): String {
    return Instant.ofEpochMilli(timeInMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
}

private fun formatDate(timeInMillis: Long): String {
    return Instant.ofEpochMilli(timeInMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("EEE, d MMM", Locale.getDefault()))
}

