package controller

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import org.xml.sax.SAXException
import service.XmlFormatter
import service.XmlParser
import service.XmlValidator
import java.io.File
import javax.xml.transform.TransformerException
import kotlin.jvm.Throws


class MainController {

    private val xmlParser = XmlParser()

    @Throws(StackOverflowError::class)
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

    fun getXmlString(): String {
        return try {
            XmlFormatter.format(xmlParser.getXmlString(false))
        }catch (e: TransformerException){
            this.xmlParser.getXmlString(true);
        }
    }

    @Throws(SAXException::class)
    fun validateXml(xml: String) {
        XmlValidator.validate(xml)
    }

    @Throws(SAXException::class)
    fun validateXml(file: File) {
        XmlValidator.validate(file)
    }
}