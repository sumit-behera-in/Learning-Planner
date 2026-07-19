package `in`.sumit.learningplanner.domain.model

data class SubTask(
    val id: Long = 0,
    val taskId: Long,
    val type: SubTaskType,
    val content: String,
    val isCompleted: Boolean = false
)

