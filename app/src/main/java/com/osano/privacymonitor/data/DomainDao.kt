package com.osano.privacymonitor.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.osano.privacymonitor.models.Domain
import java.util.*

@Dao
interface DomainDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(domain: Domain) : Long

    @Query ( value = "UPDATE domains SET score = :score, previousScore = :previousScore, lastVisited = :lastVisited WHERE rootDomain = :rootDomain")
    fun updateDomain(rootDomain: String, score: Int, previousScore: Int, lastVisited: Calendar = Calendar.getInstance())

    @Query ( value = "UPDATE domains SET lastVisited = :lastVisited WHERE rootDomain = :rootDomain")
    fun updateLastVisitedDate(rootDomain: String, lastVisited: Calendar = Calendar.getInstance())

    @Query( value = "SELECT * FROM domains ORDER BY lastVisited")
    fun getDomains(): List<Domain>

    @Query ( value = "SELECT * FROM domains WHERE rootDomain = :rootDomain")
    fun getDomain(rootDomain: String): Domain?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(domains: List<Domain>)
}