package service

import java.io.StringReader
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import kotlin.jvm.Throws

class XmlFormatter {
    /*
    * tylko formatowanie jest wykonywane za pomoca wbudowanych narzedzi
    */
    companion object {
        @Throws(TransformerException::class)
        fun format(xmlString: String): String {
            val xmlInput = StreamSource(StringReader(xmlString))

            val tform = TransformerFactory.newInstance()
            val newTransformer = tform.newTransformer()

            newTransformer.setOutputProperty(OutputKeys.INDENT, "yes")
            newTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

            val result = StreamResult(StringWriter())
            newTransformer.transform(xmlInput, result)

            return result.writer.toString()
        }
    }
}