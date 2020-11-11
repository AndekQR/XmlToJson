package service

import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class XmlFormatter {
    /*
    * tylko formatowanie jest wykonywane za pomoca wbudowanych narzedzi
    */
    companion object {
        fun format(xmlString: String): String {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val inputStream = xmlString.byteInputStream()
            val document = builder.parse(inputStream)
            document.documentElement.normalize()

            val tform = TransformerFactory.newInstance()
            val newTransformer = tform.newTransformer()

            newTransformer.setOutputProperty(OutputKeys.INDENT, "yes")
            newTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

            val result = StreamResult(StringWriter())
            newTransformer.transform(DOMSource(document), result)
            return result.writer.toString()
        }
    }
}