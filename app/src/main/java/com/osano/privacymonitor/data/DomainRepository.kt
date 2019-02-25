package com.osano.privacymonitor.data

/**
 * Repository module for handling data operations.
 */
class DomainRepository private constructor(private val domainDao: DomainDao) {

    fun getDomains() = domainDao.getDomains()

    fun getDomain(rootDomain: String) = domainDao.getDomain(rootDomain)

    companion object {

        // For singleton instantiation
        @Volatile private var instance: DomainRepository? = null

        fun getInstance(domainDao: DomainDao) =
                instance ?: synchronized(this) {
                    instance ?: DomainRepository(domainDao).also { instance = it }
                }
    }
}