package `in`.sumit.learningplanner.ui.screens.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.sumit.learningplanner.data.csv.CsvImporter
import `in`.sumit.learningplanner.data.preferences.AppPreferences
import `in`.sumit.learningplanner.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val csvImporter: CsvImporter,
    private val taskRepository: TaskRepository
) : ViewModel() {

    val isDarkMode = appPreferences.isDarkMode

    private val _importStatus = MutableStateFlow<String?>(null)
    val importStatus: StateFlow<String?> = _importStatus

    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            appPreferences.setDarkMode(isDark)
        }
    }

    fun importCsv(uri: Uri) {
        viewModelScope.launch {
            _importStatus.value = "Importing..."
            val result = csvImporter.importCsv(uri)
            result.onSuccess { count ->
                _importStatus.value = "Successfully imported $count tasks"
            }.onFailure { e ->
                _importStatus.value = "Import failed: ${e.message}"
            }
        }
    }
    
    fun clearData() {
        viewModelScope.launch {
            taskRepository.clearAllTasks()
        }
    }
}

