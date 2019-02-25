package com.osano.privacymonitor.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_urls",
    indices = [Index(value = ["url"], unique = true)]
)
data class FavoriteURL(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String
) {

    override fun toString() = url
}