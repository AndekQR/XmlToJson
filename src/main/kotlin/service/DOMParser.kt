package service

import model.NodeVisitor
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class DOMParser {

//    private val visitors: ArrayList<NodeVisitor> = ArrayList()
//
//    fun addVisitor(visitor: NodeVisitor) = this.visitors.add(visitor)
//
//    fun parse(path: String) {
//        val rootNode = parseXml(path)
//        this.search(rootNode)
//        this.afterDocument()
//    }
//
//    private fun search(rootNode: Node) {
//        if(rootNode.nodeType != Node.TEXT_NODE) {
//            this.visit(rootNode)
//            val childNodes = rootNode.childNodes
//            if(childNodes != null) {
//                for(i in 0 until childNodes.length) {
//                    val child = childNodes.item(i)
//                    if(child.nodeType != Node.TEXT_NODE)
//                        this.search(child)
//                }
//            }
//            this.afterChildVisit(rootNode)
//        }
//    }
//
//    private fun visit(xmlNode: Node) {
//        for (visitor in visitors) {
//            visitor.visit(xmlNode)
//        }
//    }
//
//    private fun afterChildVisit(xmlNode: Node) {
//        for (visitor in visitors) {
//            visitor.afterChildVisit(xmlNode)
//        }
//    }
//
//    private fun afterDocument() {
//        for (visitor in visitors) {
//            visitor.afterDocument()
//        }
//    }
//
//    private fun parseXml(path: String): Element {
//        val factory = DocumentBuilderFactory.newInstance()
//        val builder: DocumentBuilder
//        val document: Document
//        try {
//            builder = factory.newDocumentBuilder()
//            document = builder.parse(path)
//        } catch (e: Exception){
//            throw RuntimeException(e)
//        }
//
//        return document.documentElement
//    }
}