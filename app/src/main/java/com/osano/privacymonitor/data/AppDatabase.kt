package com.osano.privacymonitor.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.osano.privacymonitor.models.Domain
import com.osano.privacymonitor.models.FavoriteURL
import com.osano.privacymonitor.util.Constants

@Database(entities = [Domain::class, FavoriteURL::class], version = 3, exportSchema = false)

@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun domainDao(): DomainDao
    abstract fun favoriteURLDao(): FavoritesDao

    companion object {

        // For singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, Constants.roomDatabaseName)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}