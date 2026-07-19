package `in`.sumit.learningplanner.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import `in`.sumit.learningplanner.data.local.entity.SubTaskEntity
import `in`.sumit.learningplanner.data.local.entity.TaskEntity

data class TaskWithSubTasks(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val subTasks: List<SubTaskEntity>
)

