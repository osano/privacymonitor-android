package com.osano.privacymonitor.networking

import com.osano.privacymonitor.models.Domain
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkRepository {

    companion object {

        fun fetchDomainScore(query: String, callback: (ApiResponse<Domain>) -> Unit) {

            val call = NetworkDataSource.fetchDomainScore(query)

            call.enqueue(object : Callback<Domain> {
                override fun onResponse(call: Call<Domain>, response: Response<Domain>) {
                    callback(ApiResponse.create(response))
                }

                override fun onFailure(call: Call<Domain>, throwable: Throwable) {
                    callback(ApiResponse.create(throwable))
                }
            })
        }

        fun requestScoreAnalysis(query: String, callback: (ApiResponse<Void>) -> Unit) {

            val call = NetworkDataSource.requestScoreAnalysis(query)

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    callback(ApiResponse.create(response))
                }

                override fun onFailure(call: Call<Void>, throwable: Throwable) {
                    callback(ApiResponse.create(throwable))
                }
            })
        }
    }

}