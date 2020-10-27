package jces1209.vu.api

import java.net.URI

abstract class CloudApiClient(url: URI) : BaseApiClient(url) {

    override fun getUser(): String {
        return ""
    }

    override fun getPassword(): String {
        return ""
    }
}
