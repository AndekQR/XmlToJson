package service

import model.Node
import java.util.function.Function
import java.util.stream.Collectors


class MyJsonCreator(private val treeNode: TreeNode) {

    private var json: StringBuilder = StringBuilder()
    private var wasLastCloseBrace: Boolean = false

    init {
        this.generateJSON()
    }

    private fun generateJSON() {
        addOpenBrace()
        treeNode.rootNode?.let { search(it) }
        addCloseBrace(true)
        println(json.toString())
    }

    private fun search(node: Node, inTable: Boolean = false) {
        addNode(node, inTable)
        //lista z list z węzłami. w liście wewnętrznej jest tyle wezłów z danym imieniem ile jego powtórzeń
        val sameNameChilds = node.childs.stream()
            .collect(Collectors.groupingBy(Node::name, Collectors.mapping(Function.identity(), Collectors.toList())))
            .entries
            .stream()
            .map { entry -> entry.value }
            .collect(Collectors.toList())

        for (i in 0 until sameNameChilds.size) {
            if (sameNameChilds[i].size == 1) {
                search(sameNameChilds[i].first())
            } else if (sameNameChilds[i].size > 1) {
                addTable(sameNameChilds[i].toList())
            }
        }

        addCloseBrace()
    }

    fun getJSON(): String {
        return json.toString()
    }

    private fun addTable(elements: List<Node>) {
        this.wasLastCloseBrace = false
        json.append("\"" + elements[0].name + "\": ")
        addOpenTableBrace()
        for (i in elements.indices) {
            search(elements[i], true)
        }
        addCloseTableBrace()
    }

    private fun addNode(node: Node, inTable: Boolean) {
        this.wasLastCloseBrace = false
        if (!inTable) {
            json.append("\"" + node.name + "\":")
        }
        addOpenBrace()
        node.value.ifPresent { json.append("\"value\": \"$it\"") }

        node.atributes.forEach {
            json.append("\"_${it.key}\": \"${it.value}\",")
        }
    }

    private fun addOpenBrace() {
        json.append("{")
    }

    private fun addCloseBrace(last: Boolean = false) {
        if (wasLastCloseBrace) {
            deleteLastAddedComma()
        }
        wasLastCloseBrace = true
        json.append("}")
        if (!last) addComma()
    }

    private fun deleteLastAddedComma() {
        val c = json[json.lastIndex]
        if (c == ',') {
            json.deleteCharAt(json.lastIndex)
        }
    }

    private fun addOpenTableBrace() {
        json.append("[")
    }

    private fun addCloseTableBrace() {
        if (wasLastCloseBrace) {
            deleteLastAddedComma()
        }
        wasLastCloseBrace = true
        json.append("]")
    }

    private fun addComma() {
        json.append(",")
    }
}