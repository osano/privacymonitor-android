package com.osano.privacymonitor.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "domains")
data class Domain(
    @PrimaryKey var rootDomain: String,
    val name: String,
    val score: Int = 0,
    val previousScore: Int = 0,
    var lastVisited: Calendar
) {

    override fun toString() = rootDomain
}