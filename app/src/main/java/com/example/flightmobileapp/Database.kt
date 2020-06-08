package com.example.flightmobileapp

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

abstract class Database : RoomDatabase() {
    abstract fun Database(): Database

    companion object {
        private var INSTANCE: Database? = null

        fun getInstance(context: Context): Database? {
            if (INSTANCE == null) {
                synchronized(Database::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        Database::class.java, "urls.db").build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}