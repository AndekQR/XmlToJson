package controller

import org.w3c.dom.Node
import service.JsonVisitor
import service.Parser

class MainController {

    fun getRootNode(path: String): JsonVisitor {
       val parser = Parser()
        val jsonVisitor = JsonVisitor()
        parser.addVisitor(jsonVisitor)
        parser.parse(path)
        return jsonVisitor
    }
}