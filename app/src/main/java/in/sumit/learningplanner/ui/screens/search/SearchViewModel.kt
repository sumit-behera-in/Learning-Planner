package `in`.sumit.learningplanner.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.sumit.learningplanner.data.repository.TaskRepository
import `in`.sumit.learningplanner.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Task>>(emptyList())
    val searchResults: StateFlow<List<Task>> = _searchResults

    fun search(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            val allTasks = taskRepository.getAllTasks().first()
            _searchResults.value = allTasks.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.listName.contains(query, ignoreCase = true) ||
                it.objective.contains(query, ignoreCase = true)
            }
        }
    }
}

