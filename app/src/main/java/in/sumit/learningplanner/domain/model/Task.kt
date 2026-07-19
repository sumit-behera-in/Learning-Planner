package `in`.sumit.learningplanner.domain.model

data class Task(
    val id: Long = 0,
    val title: String,
    val listName: String,
    val date: Long,
    val objective: String,
    val estimatedTime: String,
    val theory: String,
    val docs: String,
    val course: String,
    val youtube: String,
    val exercise: String,
    val interviewPrep: String,
    val deliverable: String,
    val isCompleted: Boolean = false,
    val reminderTime: Long? = null,
    val subtasks: List<SubTask> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

val Task.completedSubtaskCount: Int
    get() = subtasks.count { it.isCompleted }

val Task.totalSubtaskCount: Int
    get() = subtasks.size

val Task.progressPercentage: Float
    get() = if (totalSubtaskCount == 0) 0f else completedSubtaskCount.toFloat() / totalSubtaskCount

