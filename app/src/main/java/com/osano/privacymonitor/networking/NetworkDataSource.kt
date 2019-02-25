package com.osano.privacymonitor.networking

import com.google.gson.JsonObject
import com.osano.privacymonitor.models.Domain
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkDataSource {

    private val apiService: PrivacyMonitorService by lazy {
        create()
    }

    private fun create(): PrivacyMonitorService {
        val retrofit = Retrofit.Builder()
            .baseUrl(PrivacyMonitorService.HTTP_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(PrivacyMonitorService::class.java)
    }

    fun fetchDomainScore(query: String): Call<Domain> {
        return apiService.getDomainScore(query)
    }

    fun requestScoreAnalysis(query: String): Call<Void> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("domain", query)

        return apiService.requestScoreAnalysis(jsonObject)
    }
}