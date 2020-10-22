package controller

import service.JsonCreator
import service.DOMParser
import service.MyJsonCreator
import service.XmlParser

class MainController {

    fun getRootNode(path: String): JsonCreator {
//       val parser = DOMParser()
        val xmlParser = XmlParser()
        val jsonVisitor = JsonCreator()
        val parseFile = xmlParser.parseFile(path)
        parseFile.addVisitor(jsonVisitor)
        parseFile.initVisitorsActions()
        MyJsonCreator(parseFile)

//        parser.addVisitor(jsonVisitor)
//        parser.parse(path)
        return jsonVisitor
    }
}