package jces1209.vu

import java.io.StringReader
import java.util.Base64
import javax.json.Json
import javax.json.JsonReader

class TrafficDataParser {

    companion object {

        fun parseData(hostName: String, readEnvTrafficShapeConfig: String): String {
            val decodedBytes = Base64.getDecoder().decode(readEnvTrafficShapeConfig)
            val decodedString = String(decodedBytes)
            val jsonReader: JsonReader = Json.createReader(StringReader(decodedString))
            var propertiesFileName = ""
            jsonReader.use {
                val obj: javax.json.JsonObject = jsonReader.readObject()
                propertiesFileName = obj.getString(hostName)
            }
            return propertiesFileName
        }
    }
}
