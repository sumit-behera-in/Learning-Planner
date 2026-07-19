package `in`.sumit.learningplanner.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.sumit.learningplanner.data.repository.TaskRepository
import `in`.sumit.learningplanner.domain.model.DashboardStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _stats = MutableStateFlow(DashboardStats())
    val stats: StateFlow<DashboardStats> = _stats

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            taskRepository.getAllTasks().collect { tasks ->
                val total = tasks.size
                val completed = tasks.count { it.isCompleted }
                val currentTime = System.currentTimeMillis()
                
                // Very basic stat calculation for now
                val overdue = tasks.count { !it.isCompleted && it.date < currentTime - 86400000 }
                
                _stats.value = DashboardStats(
                    totalTaskCount = total,
                    completedTaskCount = completed,
                    overdueTaskCount = overdue
                )
            }
        }
    }
}

