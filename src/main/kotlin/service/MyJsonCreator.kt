package service

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import model.Node

import java.util.function.Function
import java.util.stream.Collectors



class MyJsonCreator(private val treeNode: TreeNode) {

    private var json: StringBuilder = StringBuilder()

    init {
        this.generateJSON()
    }

    private fun generateJSON() {
        addOpenBrace()
        treeNode.getRootNode()?.let { search(it) }
        addCloseBrace(true)
//        val gson = GsonBuilder().setPrettyPrinting().create()
//        val jp = JsonParser.parseString(json.toString())
//        println(gson.toJson(jp))
        println(json.toString())
    }

    private fun search(node: Node, inTable: Boolean = false) {
        addNode(node, inTable)
        val sameNameChilds = node.childs.stream()
                .collect(Collectors.groupingBy(Node::name, Collectors.mapping(Function.identity(), Collectors.toList())))
                .entries
                .stream()
                .map { entry -> entry.value }
                .collect(Collectors.toList())

        for(i in 0 until sameNameChilds.size) {
            if(sameNameChilds[i].size == 1) {
                search(sameNameChilds[i].first())
            } else if(sameNameChilds[i].size > 1) {
                addTable(sameNameChilds[i].toList())
            }
        }

        addCloseBrace()
    }

    fun getJSON(): String {
        return json.toString()
    }

    private fun addTable(elements: List<Node>) {
        json.append("\"" + elements[0].name + "\": ")
        addOpenTableBrace()
        for(i in elements.indices) {
            search(elements[i], true)
        }
        addCloseTableBrace()
    }

    private fun addNode(node: Node, inTable: Boolean) {
        if(!inTable) {
            json.append("\"" + node.name + "\":" )
        }
        addOpenBrace()
        node.value.ifPresent { json.append("\"value\": \"$it\",\n") }

        node.atributes.forEach{
            json.append("\"_${it.key}\": \"${it.value}\",\n")
        }
    }

    private fun addOpenBrace() {
        json.append("{\n")
    }
    private fun addCloseBrace(last: Boolean = false) {
        json.append("}\n")
        if(!last) addComma()
    }
    private fun addOpenTableBrace() {
        json.append("[\n")
    }
    private fun addCloseTableBrace() {
        json.append("]\n")
    }
    private fun addComma() {
        json.append(",\n")
    }
}