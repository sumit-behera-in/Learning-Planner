package `in`.sumit.learningplanner.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import `in`.sumit.learningplanner.data.local.AppDatabase
import `in`.sumit.learningplanner.data.local.dao.CompletionHistoryDao
import `in`.sumit.learningplanner.data.local.dao.SubTaskDao
import `in`.sumit.learningplanner.data.local.dao.TaskDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideTaskDao(appDatabase: AppDatabase): TaskDao {
        return appDatabase.taskDao()
    }

    @Provides
    fun provideSubTaskDao(appDatabase: AppDatabase): SubTaskDao {
        return appDatabase.subTaskDao()
    }

    @Provides
    fun provideCompletionHistoryDao(appDatabase: AppDatabase): CompletionHistoryDao {
        return appDatabase.completionHistoryDao()
    }
}

