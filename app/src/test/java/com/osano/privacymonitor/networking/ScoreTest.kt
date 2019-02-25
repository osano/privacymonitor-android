package com.osano.privacymonitor.networking

import com.google.gson.JsonObject
import com.osano.privacymonitor.util.JsonUtil
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ScoreTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: PrivacyMonitorService

    @Before
    fun createService() {

        mockWebServer = MockWebServer()
        mockWebServer.start()

        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PrivacyMonitorService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun testFetchPrivacyMonitorScore() {

        val mockResponse: MockResponse = MockResponse().setResponseCode(200).setBody(JsonUtil.getJSON("privacymonitor.json"))
        mockWebServer.enqueue(mockResponse)

        val call = service.getDomainScore("privacymonitor.com")
        val response = call.execute()
        val domain = response.body()

        Assert.assertNotNull(domain)
        Assert.assertThat(domain?.name, IsEqual.equalTo("Privacy  Monitor"))
        Assert.assertThat(domain?.score, IsEqual.equalTo(845))
        Assert.assertThat(domain?.previousScore, IsEqual.equalTo(505))
    }

    @Test
    fun testFetchMicrosoftScore() {

        val mockResponse: MockResponse = MockResponse().setResponseCode(200).setBody(JsonUtil.getJSON("microsoft.json"))
        mockWebServer.enqueue(mockResponse)

        val call = service.getDomainScore("microsoft.com")
        val response = call.execute()
        val domain = response.body()

        Assert.assertNotNull(domain)
        Assert.assertThat(domain?.name, IsEqual.equalTo("Microsoft"))
        Assert.assertThat(domain?.score, IsEqual.equalTo(714))
        Assert.assertThat(domain?.previousScore, IsEqual.equalTo(714))
    }

    @Test
    fun testFetchGoogleScore() {

        val mockResponse: MockResponse = MockResponse().setResponseCode(200).setBody(JsonUtil.getJSON("google.json"))
        mockWebServer.enqueue(mockResponse)

        val call = service.getDomainScore("google.com")
        val response = call.execute()
        val domain = response.body()

        Assert.assertNotNull(domain)
        Assert.assertThat(domain?.name, IsEqual.equalTo("Google"))
        Assert.assertThat(domain?.score, IsEqual.equalTo(601))
        Assert.assertThat(domain?.previousScore, IsEqual.equalTo(637))
    }

    @Test
    fun testFetchGitHubScore() {

        val mockResponse: MockResponse = MockResponse().setResponseCode(200).setBody(JsonUtil.getJSON("github.json"))
        mockWebServer.enqueue(mockResponse)

        val call = service.getDomainScore("github.com")
        val response = call.execute()
        val domain = response.body()

        Assert.assertNotNull(domain)
        Assert.assertThat(domain?.name, IsEqual.equalTo("GitHub"))
        Assert.assertThat(domain?.score, IsEqual.equalTo(827))
        Assert.assertThat(domain?.previousScore, IsEqual.equalTo(839))
    }

    @Test
    fun testFetchWebexScore() {

        val mockResponse: MockResponse = MockResponse().setResponseCode(404).setBody(JsonUtil.getJSON("webex.json"))
        mockWebServer.enqueue(mockResponse)

        val call = service.getDomainScore("webex.com")
        val response = call.execute()
        val domain = response.body()

        Assert.assertNull(domain)
    }

    @Test
    fun testRequestScoreAnalysis() {

        val mockResponse: MockResponse = MockResponse().setResponseCode(200).setBody(JsonUtil.getJSON("request_score_analysis.json"))
        mockWebServer.enqueue(mockResponse)

        val jsonObject = JsonObject()
        jsonObject.addProperty("domain", "webex.com")

        val call = service.requestScoreAnalysis(jsonObject)
        val response = call.execute()

        Assert.assertNull(response.body())
    }
}