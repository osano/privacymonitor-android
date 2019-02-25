package com.osano.privacymonitor.data

import androidx.room.*
import com.osano.privacymonitor.models.FavoriteURL

@Dao
interface FavoritesDao {
    @Query( value = "SELECT * FROM favorite_urls ORDER BY id")
    fun getUrls(): List<FavoriteURL>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(url: FavoriteURL) : Long

    @Delete
    fun delete(url: FavoriteURL)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(urls: List<FavoriteURL>)

    @Query("DELETE FROM favorite_urls")
    fun deleteAll()
}