package controller

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import service.XmlFormatter
import service.XmlParser


class MainController {

    private val xmlParser = XmlParser()

    fun getJson(path: String, prettyFormat: Boolean): String {
        xmlParser.parseFile(path)
        val json = this.xmlParser.getJson()
        return if (prettyFormat) this.getPrettyFormattedJson(json)
        else json
    }

    private fun getPrettyFormattedJson(json: String): String {
        val jsonObject = JsonParser.parseString(json).asJsonObject
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(jsonObject)
    }

    fun convertXmlStringToJson(xml: String, prettyFormat: Boolean): String {
        this.xmlParser.parseXmlString(xml)
        val json = this.xmlParser.getJson()
        return if (prettyFormat) this.getPrettyFormattedJson(json)
        else json
    }

    fun getFormattedXml(): String {
        return XmlFormatter.format(xmlParser.getXmlString())
    }
}