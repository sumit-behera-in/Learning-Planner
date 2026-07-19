package `in`.sumit.learningplanner.data.csv

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import `in`.sumit.learningplanner.data.local.dao.SubTaskDao
import `in`.sumit.learningplanner.data.local.dao.TaskDao
import `in`.sumit.learningplanner.data.local.entity.SubTaskEntity
import `in`.sumit.learningplanner.data.local.entity.TaskEntity
import `in`.sumit.learningplanner.domain.model.SubTaskType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class CsvImporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskDao: TaskDao,
    private val subTaskDao: SubTaskDao
) {
    suspend fun importCsv(uri: Uri): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Could not open file"))
            val reader = InputStreamReader(inputStream)
            val csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build()
            val parser = CSVParser(reader, csvFormat)

            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            var count = 0

            for (record in parser) {
                try {
                    val list = record.get("List") ?: ""
                    val dateStr = record.get("Date") ?: ""
                    val title = record.get("Title") ?: ""
                    val notes = record.get("Notes") ?: ""

                    val date = dateFormat.parse(dateStr)?.time ?: System.currentTimeMillis()
                    
                    val parsedNotes = parseNotes(notes)

                    val taskEntity = TaskEntity(
                        title = title,
                        listName = list,
                        date = date,
                        objective = parsedNotes["OBJECTIVE"] ?: "",
                        estimatedTime = parsedNotes["TIME"] ?: "",
                        theory = parsedNotes["THEORY"] ?: "",
                        docs = parsedNotes["DOCS"] ?: "",
                        course = parsedNotes["COURSE"] ?: "",
                        youtube = parsedNotes["YOUTUBE"] ?: "",
                        exercise = parsedNotes["EXERCISE"] ?: "",
                        interviewPrep = parsedNotes["INTERVIEW PREP"] ?: "",
                        deliverable = parsedNotes["DELIVERABLE"] ?: ""
                    )

                    val taskId = taskDao.insertTask(taskEntity)

                    val subTasks = mutableListOf<SubTaskEntity>()
                    fun addSubTask(type: SubTaskType, content: String?) {
                        if (!content.isNullOrBlank()) {
                            subTasks.add(SubTaskEntity(taskId = taskId, type = type.name, content = content.trim()))
                        }
                    }

                    addSubTask(SubTaskType.THEORY, parsedNotes["THEORY"])
                    addSubTask(SubTaskType.DOCS, parsedNotes["DOCS"])
                    addSubTask(SubTaskType.COURSE, parsedNotes["COURSE"])
                    addSubTask(SubTaskType.YOUTUBE, parsedNotes["YOUTUBE"])
                    addSubTask(SubTaskType.EXERCISE, parsedNotes["EXERCISE"])
                    addSubTask(SubTaskType.INTERVIEW_PREP, parsedNotes["INTERVIEW PREP"])
                    addSubTask(SubTaskType.DELIVERABLE, parsedNotes["DELIVERABLE"])

                    if (subTasks.isNotEmpty()) {
                        subTaskDao.insertSubTasks(subTasks)
                    }
                    count++
                } catch (e: Exception) {
                    // Skip problematic rows
                    e.printStackTrace()
                }
            }
            parser.close()
            Result.success(count)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun parseNotes(notes: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val keywords = listOf(
            "OBJECTIVE:", "TIME:", "THEORY:", "DOCS:", "COURSE:", 
            "YouTube search:", "EXERCISE:", "INTERVIEW PREP:", "DELIVERABLE:"
        )
        
        var currentKey = ""
        var currentValue = StringBuilder()
        
        val lines = notes.split("\n")
        for (line in lines) {
            var matchedKeyword: String? = null
            for (keyword in keywords) {
                if (line.trim().startsWith(keyword, ignoreCase = true)) {
                    matchedKeyword = keyword
                    break
                }
            }
            
            if (matchedKeyword != null) {
                if (currentKey.isNotEmpty()) {
                    result[normalizeKey(currentKey)] = currentValue.toString().trim()
                }
                currentKey = matchedKeyword
                currentValue = StringBuilder(line.substring(matchedKeyword.length).trim())
            } else {
                if (currentKey.isNotEmpty()) {
                    currentValue.append("\n").append(line)
                }
            }
        }
        
        if (currentKey.isNotEmpty()) {
            result[normalizeKey(currentKey)] = currentValue.toString().trim()
        }
        
        return result
    }

    private fun normalizeKey(key: String): String {
        return key.replace(":", "").uppercase(Locale.getDefault()).trim()
    }
}

