package `in`.sumit.learningplanner.domain.model

data class TaskFilter(
    val month: Int? = null,
    val year: Int? = null,
    val listName: String? = null,
    val status: FilterStatus? = null,
    val dateFrom: Long? = null,
    val dateTo: Long? = null
)

enum class FilterStatus { PENDING, COMPLETED, OVERDUE }

