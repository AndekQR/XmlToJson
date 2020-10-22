package service

import helper.XmlTagType
import model.Node
import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class XmlParser {

    private lateinit var reader: BufferedReader

    private fun prepareData(data: String): String {
        return data
                .trim()
                .replace("\n", "")
                .replace("\r", "")
                .replace("> +".toRegex(), ">")
    }

    private fun initFile(path: String) {
        this.reader = Files.newBufferedReader(Paths.get(path))
    }

    fun parseFile(path: String): TreeNode {
        this.initFile(path)
        val xml = this.prepareData(reader.readText())
        if(xml.isBlank()) throw RuntimeException("File missing or corrupt")
        val detector = TagTypeDetector(xml[0])
        var node: Node? = Node()
        val builder = StringBuilder()
        var lastArtName: String = ""
        val treeNode = TreeNode()
        for (i in 1 until xml.length) {
            detector.checkV2(xml[i])
//            println(xml[i] + ": " + detector.typeOfCurrent() + "(actualType: " + detector.actualTag.get().type + ")")
            when (detector.typeOfCurrent()) {
                XmlTagType.PROLOG.type,
                XmlTagType.TAG_NAME.type,
                XmlTagType.TAG_VALUE.type,
                XmlTagType.COMMENT.type,
                XmlTagType.ATTRIBUTE_NAME.type,
                XmlTagType.ATTRIBUTE_VALUE.type,
                XmlTagType.CLOSE_TAG.type,
                XmlTagType.INLINE_CLOSE_TAG.type -> builder.append(xml[i])

                XmlTagType.UNMACHED.type -> {
                    if (builder.isNotBlank())
                        when (detector.actualTag.get()) {
                            XmlTagType.TAG_NAME -> {
                                node = Node(name = builder.toString())
                                treeNode.addChild(node)
                            }
                            XmlTagType.TAG_VALUE -> treeNode.getLastNode().value = Optional.of(builder.toString())
                            XmlTagType.COMMENT -> {
                                node = Node(name = "_comment")
                                node.value = Optional.of(builder.toString())
                                treeNode.addChild(node)
                                treeNode.goToParent()
                            }
                            XmlTagType.ATTRIBUTE_NAME -> lastArtName = builder.toString()
                            XmlTagType.ATTRIBUTE_VALUE -> treeNode.getLastNode().atributes[lastArtName] = builder.toString()
                            XmlTagType.CLOSE_TAG -> {
                                treeNode.goToParent()
                            }
                            XmlTagType.INLINE_CLOSE_TAG -> {
                                node = Node(name = builder.toString())
                                treeNode.addChild(node)
                                treeNode.goToParent()
                            }
                        }
                    builder.clear()
                }

            }
        }
//        println(treeNode.getRootNode())
        return treeNode
    }
}

