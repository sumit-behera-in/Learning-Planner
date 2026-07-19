package `in`.sumit.learningplanner.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TaskModelTest {

    @Test
    fun `completed subtask count returns number of completed subtasks`() {
        val task = createTask(
            subtasks = listOf(
                createSubTask(isCompleted = true),
                createSubTask(isCompleted = false),
                createSubTask(isCompleted = true)
            )
        )

        assertEquals(2, task.completedSubtaskCount)
    }

    @Test
    fun `total subtask count returns total amount of subtasks`() {
        val task = createTask(
            subtasks = listOf(
                createSubTask(),
                createSubTask(),
                createSubTask()
            )
        )

        assertEquals(3, task.totalSubtaskCount)
    }

    @Test
    fun `progress percentage is zero when there are no subtasks`() {
        val task = createTask()

        assertEquals(0f, task.progressPercentage, 0f)
    }

    @Test
    fun `progress percentage reflects completed subtasks`() {
        val task = createTask(
            subtasks = listOf(
                createSubTask(isCompleted = true),
                createSubTask(isCompleted = false),
                createSubTask(isCompleted = true)
            )
        )

        assertEquals(0.6666667f, task.progressPercentage, 0.0001f)
    }

    @Test
    fun `task filter defaults are null`() {
        val filter = TaskFilter()

        assertNull(filter.month)
        assertNull(filter.year)
        assertNull(filter.listName)
        assertNull(filter.status)
        assertNull(filter.dateFrom)
        assertNull(filter.dateTo)
    }

    private fun createTask(subtasks: List<SubTask> = emptyList()) = Task(
        title = "Study Kotlin",
        listName = "Learning",
        date = 1710000000000L,
        objective = "Finish the module",
        estimatedTime = "2h",
        theory = "Read docs",
        docs = "Review notes",
        course = "Android basics",
        youtube = "Kotlin tutorial",
        exercise = "Make examples",
        interviewPrep = "Practice questions",
        deliverable = "Submit summary",
        subtasks = subtasks
    )

    private fun createSubTask(isCompleted: Boolean = false) = SubTask(
        taskId = 1L,
        type = SubTaskType.THEORY,
        content = "Example content",
        isCompleted = isCompleted
    )
}
