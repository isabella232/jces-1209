package jces1209.vu

import java.io.InputStream
import java.util.Properties

class ConfigProperties {

    companion object {
        lateinit var properties: Properties

        fun load(resourceName: String?): Properties {
            val fileReadStream: InputStream? = this::class.java.getResourceAsStream("/$resourceName")

            properties = Properties()
            fileReadStream?.use {
                properties.load(it)
            }
            return properties
        }
    }
}
