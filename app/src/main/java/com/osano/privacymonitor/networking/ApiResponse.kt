package com.osano.privacymonitor.networking

import retrofit2.Response

sealed class NetworkError {
    class UnknownError : NetworkError()
    class InvalidRequest : NetworkError()
    class NotFound : NetworkError()
    class ServerError : NetworkError()
    class UnderlyingError(val errorMessage: String) : NetworkError()
}

/**
 * Common class used by API responses.
 * @param <T> the type of the response object
</T> */
@Suppress("unused") // T is used in extending classes
sealed class ApiResponse<T> {
    companion object {
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            val networkError = if (error.message.isNullOrEmpty()) {
                NetworkError.UnknownError()
            }
            else {
                NetworkError.UnderlyingError(error.message ?: "unknown error")
            }

            return ApiErrorResponse(networkError)
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                }
                else {
                    ApiSuccessResponse(body = body)
                }
            }
            else {
                ApiErrorResponse(
                    errorFromResponseCode(
                        response.code()
                    )
                )
            }
        }

        private fun errorFromResponseCode(code: Int): NetworkError {
            return when (code) {
                400 -> NetworkError.InvalidRequest()
                404 -> NetworkError.NotFound()
                in 500..599 -> NetworkError.ServerError()
                else -> {
                    NetworkError.UnknownError()
                }
            }
        }
    }
}

class ApiEmptyResponse<T> : ApiResponse<T>()

data class ApiSuccessResponse<T>(val body: T) : ApiResponse<T>()
data class ApiErrorResponse<T>(val networkError: NetworkError) : ApiResponse<T>()