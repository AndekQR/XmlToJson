package service

import model.Node
import model.NodeVisitor

class JsonCreator: NodeVisitor {

    private val json = StringBuilder()

    init {
        json.append("{\n")
    }

    override fun visit(node: Node) {
        json.append("\""+node.name+"\""+": {\n")
    }

    override fun afterChildVisit(node: Node) {
        json.append("}\n")
    }

    fun getJsonString(): String {
        return json.toString()
    }

    override fun afterDocument() {
        json.append("}")
    }
}