package jces1209.vu.api

import com.google.gson.Gson
import okhttp3.Credentials
import okhttp3.Request
import okhttp3.Response
import java.net.URI

abstract class CloudApiClient(url: URI) : BaseApiClient(url) {

    val batchSize = 50

    override fun getUser(): String {
        return CloudSettings.USER
    }

    override fun getPassword(): String {
        return CloudSettings.PASSWORD
    }

    protected fun delete(uriPath: String): Pair<Request, Response> {
        val request = getRequestBuilder(uriPath)
            .delete()
            .build()
        val response = okHttpClient.newCall(request).execute()
        println("Delete [$uriPath]. Response code [${response.code()}]. Body [${response.body()?.string()}], Request [$request]")
        return Pair(request, response)
    }

    private fun getRequestBuilder(uriPath: String): Request.Builder {
        return Request.Builder()
            .url("$url$uriPath")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", Credentials.basic(getUser(), getPassword()))
    }

    protected fun <T> get(uri: String, clazz: Class<T>): T {
        val request = getRequestBuilder(uri)
            .get()
            .build()
        val response = okHttpClient.newCall(request).execute().body()?.string()
        println("Get request [$request] - Response [$response]")
        return Gson().fromJson<T>(response, clazz)
    }
}
