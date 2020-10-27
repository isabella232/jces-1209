package jces1209.vu.api

import okhttp3.OkHttpClient
import java.net.URI


abstract class BaseApiClient(
    protected val url: URI
): ApiClient {
    protected val okHttpClient = OkHttpClient()

    // TODO Get credentials from properties
    abstract fun getUser(): String?
    abstract fun getPassword(): String?

    override fun isReady(): Boolean {
        return getUser()?.isNotEmpty() ?: false && getPassword()?.isNotEmpty() ?: false
    }
}
