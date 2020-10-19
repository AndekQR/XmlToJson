package service

import model.Node
import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class RegexParser(path: String) {


    private val filePath = path
    private val reader: BufferedReader
    private val rootNode: Node? = null
    private val notClosedNodesNames: Stack<String> = Stack()

    init {
        reader = Files.newBufferedReader(Paths.get(this.filePath))
        parseFile()
    }

    private fun parseFile() {
        val xml = reader.readText().trim().replace("\n", "").replace("\r", "").replace("> +".toRegex(), ">")
        val detector = TagTypeDetector(xml[0])
        for (i in 1 until xml.length) {
            detector.checkV2(xml[i])
            println(xml[i]+": "+detector.typeOfCurrent()+"(actualType: "+detector.actualTag.get().type+")")
        }
    }
}
