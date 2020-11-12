package service

import org.w3c.dom.Document
import org.xml.sax.SAXException
import java.io.File
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.jvm.Throws

class XmlValidator {
    companion object {
        @Throws(SAXException::class)
        fun validate(xml: String) {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val inputStream = xml.byteInputStream()
            builder.parse(inputStream)
        }

        @Throws(SAXException::class, IOException::class)
        fun validate(file: File) {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            builder.parse(file)
        }
    }
}