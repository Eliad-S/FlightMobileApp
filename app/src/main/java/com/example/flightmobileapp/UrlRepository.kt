package com.example.flightmobileapp

import androidx.lifecycle.LiveData

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class UrlRepository(private val urlDao: UrlDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allUrls: LiveData<List<Url>> = urlDao.getAllUrls()

    suspend fun insert(url: Url) {
        urlDao.insert(url)
    }
    fun updateNumbers() {
        urlDao.updateNumbers()
    }
}
