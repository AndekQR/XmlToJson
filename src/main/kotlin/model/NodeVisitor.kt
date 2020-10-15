package model

import org.w3c.dom.Node

interface NodeVisitor {

    fun visit(node: Node)
    fun afterChildVisit(node: Node)
    fun afterDocument()

}