package com.example.flightmobileapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UrlDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(url: Url)

    @Query("SELECT * FROM url_table ORDER BY number ASC LIMIT 5")
    fun getAllUrls(): LiveData<List<Url>>

    //after every insertion, we must call this function in order keep LRU cache architecture
    @Query("UPDATE url_table SET number = number + 1")
    fun updateNumbers()

    @Query("DELETE FROM url_table")
    suspend fun deleteAll()

//    @Query("IF EXIST (SELECT * FROM url_table)")
//    suspend fun checkIfUrlExist()
}