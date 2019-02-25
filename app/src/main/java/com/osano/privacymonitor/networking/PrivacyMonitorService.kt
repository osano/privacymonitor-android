package com.osano.privacymonitor.networking

import com.google.gson.JsonObject
import com.osano.privacymonitor.models.Domain
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * REST API access points
 */

interface PrivacyMonitorService {

    companion object {
        const val HTTP_API_BASE_URL = "https://api.privacymonitor.com/"
    }

    @GET("score")
    fun getDomainScore(@Query("q") query: String): Call<Domain>

    @POST("analysis")
    fun requestScoreAnalysis(@Body body: JsonObject): Call<Void>
}