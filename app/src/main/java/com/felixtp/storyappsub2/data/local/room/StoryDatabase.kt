package com.felixtp.storyappsub2.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.felixtp.storyappsub2.data.remote.response.ListStoryItem

@Database(
    entities = [ListStoryItem::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: createDatabase(context.applicationContext).also { INSTANCE = it }
            }
        }

        private fun createDatabase(appContext: Context): StoryDatabase {
            return Room.databaseBuilder(
                appContext,
                StoryDatabase::class.java,
                "story_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
