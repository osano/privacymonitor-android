package com.osano.privacymonitor.application

import android.content.Context
import com.osano.privacymonitor.data.AppDatabase
import com.osano.privacymonitor.data.DomainDao
import com.osano.privacymonitor.models.Domain
import com.osano.privacymonitor.networking.ApiResponse
import com.osano.privacymonitor.networking.ApiSuccessResponse
import com.osano.privacymonitor.networking.NetworkRepository
import java.util.*

class PrivacyMonitorRepository constructor(private val context: Context) {

    lateinit var domainDao: DomainDao

    fun requestDomainScore(rootDomain: String, visitedDate: Calendar = Calendar.getInstance(), callback: (ApiResponse<Domain>) -> Unit) {
        val database = AppDatabase.getInstance(context)
        domainDao = database.domainDao()

        // Check if domain exists locally
        val domain = domainDao.getDomain(rootDomain)

        // Request domain info from API.
        NetworkRepository.fetchDomainScore(rootDomain) {
            when(it) {
                is ApiSuccessResponse<Domain> -> {
                    // Domain found, insert or update in the local database.
                    if (domain == null) {
                        it.body.rootDomain = rootDomain
                        it.body.lastVisited = visitedDate
                        domainDao.insert(it.body)
                    }
                    else {
                        domainDao.updateDomain(rootDomain, score = it.body.score, previousScore = it.body.previousScore, lastVisited = visitedDate)
                    }
                }
            }

            callback(it)
        }
    }

    fun registerDomainVisit(rootDomain: String, visitedDate: Calendar = Calendar.getInstance(), callback: (Domain) -> Unit) {
        val database = AppDatabase.getInstance(context)
        domainDao = database.domainDao()

        // Check if domain exists locally
        val domain = domainDao.getDomain(rootDomain)

        if (domain != null) {
            // Domain available from local storage, check last visited date (>29 days) or score 0
            val differenceMillis = domain.lastVisited.timeInMillis - visitedDate.timeInMillis
            val days = (differenceMillis / (1000 * 60 * 60 * 24)).toInt()
            if (days > 29 || domain.score == 0) {

                // Request domain info from API.
                NetworkRepository.fetchDomainScore(rootDomain) {
                    when(it) {
                        // Domain found, update score in the local database.
                        is ApiSuccessResponse<Domain> -> {
                            domainDao.updateDomain(rootDomain, score = it.body.score, previousScore = it.body.previousScore, lastVisited = visitedDate)
                            callback(it.body)
                        }
                    }
                }
            }
            else {
                // Domain recently visited, let's update its last visited date.
                domainDao.updateLastVisitedDate(rootDomain, visitedDate)
            }
        }
        else {
            // Domain not available from local storage, fetch from API and store.
            NetworkRepository.fetchDomainScore(rootDomain) {
                when(it) {
                    // Domain found, store it in the local database.
                    is ApiSuccessResponse<Domain> -> {
                        it.body.rootDomain = rootDomain
                        it.body.lastVisited = visitedDate
                        domainDao.insert(it.body)
                        callback(it.body)
                    }
                }
            }
        }
    }
}