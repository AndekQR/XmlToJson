package model

interface NodeVisitor {

    fun visit(node: Node)
    fun afterChildVisit(node: Node)
    fun afterDocument()

}