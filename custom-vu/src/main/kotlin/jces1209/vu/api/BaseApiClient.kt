package jces1209.vu.api

import okhttp3.OkHttpClient
import java.net.URI
import java.util.concurrent.TimeUnit


abstract class BaseApiClient(
    protected val url: URI
): ApiClient {
    protected val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // TODO Get credentials from properties
    abstract fun getUser(): String?
    abstract fun getPassword(): String?

    override fun isReady(): Boolean {
        return getUser()?.isNotEmpty() ?: false && getPassword()?.isNotEmpty() ?: false
    }
}
