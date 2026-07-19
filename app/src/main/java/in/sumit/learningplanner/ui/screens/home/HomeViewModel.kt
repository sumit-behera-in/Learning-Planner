package `in`.sumit.learningplanner.ui.screens.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.sumit.learningplanner.data.csv.CsvImporter
import `in`.sumit.learningplanner.data.repository.TaskRepository
import `in`.sumit.learningplanner.domain.model.Task
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val csvImporter: CsvImporter
) : ViewModel() {

    val tasks: StateFlow<List<Task>> = taskRepository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _importEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val importEvents: SharedFlow<String> = _importEvents.asSharedFlow()

    fun updateSubTaskCompletion(subTaskId: Long, isCompleted: Boolean, taskId: Long) {
        viewModelScope.launch {
            taskRepository.updateSubTaskCompletion(subTaskId, isCompleted, taskId)
        }
    }

    fun completeTask(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId)
            task?.subtasks?.forEach { subTask ->
                taskRepository.updateSubTaskCompletion(subTask.id, isCompleted, taskId)
            }
        }
    }

    fun importCsv(uri: Uri) {
        viewModelScope.launch {
            val result = csvImporter.importCsv(uri)
            result.onSuccess { count ->
                _importEvents.emit("Successfully imported $count tasks")
            }.onFailure { e ->
                _importEvents.emit("Import failed: ${e.message}")
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
        }
    }
}

