package `in`.sumit.learningplanner.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.sumit.learningplanner.data.repository.TaskRepository
import `in`.sumit.learningplanner.domain.model.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    val tasks: StateFlow<List<Task>> = taskRepository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSubTaskCompletion(subTaskId: Long, isCompleted: Boolean, taskId: Long) {
        viewModelScope.launch {
            taskRepository.updateSubTaskCompletion(subTaskId, isCompleted, taskId)
        }
    }

    fun completeTask(taskId: Long, isCompleted: Boolean) {
        // Automatically completes all subtasks if task is marked complete from root
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId)
            task?.subtasks?.forEach { subTask ->
                taskRepository.updateSubTaskCompletion(subTask.id, isCompleted, taskId)
            }
        }
    }
    
    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
        }
    }
}

