package service

import model.NodeVisitor
import org.w3c.dom.Node

class JsonVisitor: NodeVisitor {

    private val json = StringBuilder()

    init {
        json.append("{")
    }

    override fun visit(node: Node) {
        json.append(node.nodeName+": {")
    }

    override fun afterChildVisit(node: Node) {
        json.append("}")
    }

    fun getJsonString(): String {
        return json.toString()
    }

    override fun afterDocument() {
        json.append("}")
    }
}