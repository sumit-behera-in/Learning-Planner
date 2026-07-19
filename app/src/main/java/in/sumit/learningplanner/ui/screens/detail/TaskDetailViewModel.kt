package `in`.sumit.learningplanner.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.sumit.learningplanner.data.repository.TaskRepository
import `in`.sumit.learningplanner.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: Long = checkNotNull(savedStateHandle["taskId"])
    
    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            _task.value = taskRepository.getTaskById(taskId)
        }
    }

    fun updateSubTaskCompletion(subTaskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.updateSubTaskCompletion(subTaskId, isCompleted, taskId)
            loadTask() // Reload to reflect changes
        }
    }
}

