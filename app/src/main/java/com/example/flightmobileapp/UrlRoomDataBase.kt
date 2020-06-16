package com.example.flightmobileapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the Url class
@Database(entities = [Url::class], version = 1, exportSchema = false)
public abstract class UrlRoomDatabase : RoomDatabase() {

    abstract fun urlDao(): UrlDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: UrlRoomDatabase? = null

        private class UrlDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.urlDao())
                    }
                }
            }

            suspend fun populateDatabase(urlDao: UrlDao) {
                // Delete all content here.
                //urlDao.deleteAll()

                // Add sample Urls.
//                var url = Url("Hello")
//                urlDao.insert(url)
//                url = Url("World!")
//                urlDao.insert(url)
                // TODO: Add your own words!
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): UrlRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UrlRoomDatabase::class.java,
                    "url_database"
                ).addCallback(UrlDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
