package controller

import service.JsonVisitor
import service.DOMParser
import service.RegexParser

class MainController {

    fun getRootNode(path: String): JsonVisitor {
       val parser = DOMParser()
        RegexParser(path)
        val jsonVisitor = JsonVisitor()
        parser.addVisitor(jsonVisitor)
        parser.parse(path)
        return jsonVisitor
    }
}