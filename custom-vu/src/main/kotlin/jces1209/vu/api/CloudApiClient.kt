package jces1209.vu.api

import okhttp3.Credentials
import okhttp3.Request
import okhttp3.Response
import java.net.URI

abstract class CloudApiClient(url: URI) : BaseApiClient(url) {

    override fun getUser(): String {
        return ""
    }

    override fun getPassword(): String {
        return ""
    }

    protected fun delete(uriPath: String): Pair<Request, Response> {
        val request = Request.Builder()
            .url("$url$uriPath")
            .delete()
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", Credentials.basic(getUser(), getPassword()))
            .build()
        val response = okHttpClient.newCall(request).execute()
        println("Delete [$uriPath]. Response code [${response.code()}]. Body [${response.body()?.string()}], Request [$request]")
        return Pair(request, response)
    }
}
