package com.osano.privacymonitor.data

import com.osano.privacymonitor.models.FavoriteURL

/**
 * Repository module for handling data operations.
 */
class FavoritesRepository private constructor(private val favoritesDao: FavoritesDao) {

    fun getUrls() = favoritesDao.getUrls()
    fun insertUrl(url: FavoriteURL) = favoritesDao.insert(url)
    fun delete(url: FavoriteURL) = favoritesDao.delete(url)

    companion object {

        // For singleton instantiation
        @Volatile private var instance: FavoritesRepository? = null

        fun getInstance(favoritesDao: FavoritesDao) =
            instance ?: synchronized(this) {
                instance ?: FavoritesRepository(favoritesDao).also { instance = it }
            }
    }
}