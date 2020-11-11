package service

import helper.XmlTagType.*
import model.Node
import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class XmlParser {

    private var treeNode: TreeNode? = null
    private lateinit var reader: BufferedReader
    private var xmlString: String = ""

    private fun prepareData(data: String): String {
        return data
            .trim()
            .replace(System.lineSeparator(), "")
            .replace("\t", "")
            .replace("> +".toRegex(), ">")
    }

    private fun initFile(path: String) {
        this.reader = Files.newBufferedReader(Paths.get(path))
    }

    fun getXmlString(): String {
        return this.xmlString
    }

    fun parseXmlString(xml: String) {
        val parse = this.parse(xml)
        this.treeNode = parse
    }

    fun getJson(): String {
        this.treeNode?.let {
            val jsonCreator = MyJsonCreator(it)
            return jsonCreator.getJSON()
        }
        throw RuntimeException("First, parse XML")
    }

    fun getTreeNode(): Optional<TreeNode> {
        return Optional.ofNullable(this.treeNode)
    }

    fun parseFile(path: String) {
        this.initFile(path)
        val xml = this.prepareData(reader.readText())
        if (xml.isBlank()) throw RuntimeException("File missing or corrupt")

        this.xmlString = xml
        this.treeNode = this.parse(xml)
    }

    private fun parse(xml: String): TreeNode {
        val detector = TagTypeDetector(xml[0])
        var node: Node?
        val builder = StringBuilder()
        var lastArtName = ""
        val treeNode = TreeNode()

        for (i in 1 until xml.length) {
            detector.checkV2(xml[i])
//            println(xml[i] + ": " + detector.typeOfCurrent() + "(actualType: " + detector.actualTag.get().type + ")")
            when (detector.typeOfCurrent()) {
                PROLOG.type,
                TAG_NAME.type,
                TAG_VALUE.type,
                COMMENT.type,
                ATTRIBUTE_NAME.type,
                ATTRIBUTE_VALUE.type,
                CLOSE_TAG.type,
                INLINE_CLOSE_TAG.type -> builder.append(xml[i])

                UNMACHED.type -> {
                    if (builder.isNotBlank())
                        when (detector.actualTag.get()) {
                            TAG_NAME -> {
                                node = Node(name = builder.toString())
                                treeNode.addChild(node)
                            }
                            TAG_VALUE -> treeNode.getLastNode().value = Optional.of(builder.toString())
                            COMMENT -> {
                                node = Node(name = "_comment")
                                node.value = Optional.of(builder.toString())
                                treeNode.addChild(node)
                                treeNode.goToParent()
                            }
                            ATTRIBUTE_NAME -> lastArtName = builder.toString()
                            ATTRIBUTE_VALUE -> treeNode.getLastNode().atributes[lastArtName] = builder.toString()
                            CLOSE_TAG -> {
                                treeNode.goToParent()
                            }
                            INLINE_CLOSE_TAG -> {
                                node = Node(name = builder.toString())
                                treeNode.addChild(node)
                                treeNode.goToParent()
                            }
                        }
                    builder.clear()
                }

            }
        }
        return treeNode
    }
}

