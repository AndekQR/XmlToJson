package service

import model.Node
import model.NodeVisitor
import java.util.*
import kotlin.collections.ArrayList

class TreeNode {

    private var rootNode: Node? = null
    private var lastAddNode: Node? = null
    private val visitors: ArrayList<NodeVisitor> = ArrayList()

    fun addVisitor(visitor: NodeVisitor) = this.visitors.add(visitor)

    fun addChild(node: Node) {
        if (rootNode == null) {
            rootNode = node
            lastAddNode = rootNode
        } else {
            node.parent = Optional.of(this.lastAddNode ?: return)
            lastAddNode?.childs?.add(node)
            lastAddNode = node
        }
    }

    fun goToParent() {
        if (lastAddNode != null && lastAddNode!!.parent.isPresent) {
            lastAddNode = lastAddNode!!.parent.get()
        } else if (lastAddNode == rootNode)
            return
        else {
            throw RuntimeException("No parent")
        }
    }

    fun getLastNode(): Node {
        if (lastAddNode != null) {
            return lastAddNode as Node
        } else throw RuntimeException("No nodes")
    }

    fun getRootNode(): Node? {
        return rootNode
    }

    fun initVisitorsActions() {
        this.rootNode?.let {
            this.search(it)
            this.afterDocument()
        }

    }

    private fun search(node: Node) {
        this.visit(node)
        val childNodes = node.childs
        for(i in 0 until childNodes.size) {
            this.search(childNodes[i])
        }
        this.afterChildVisit(node)
    }

    private fun visit(xmlNode: Node) {
        for (visitor in visitors) {
            visitor.visit(xmlNode)
        }
    }

    private fun afterChildVisit(xmlNode: Node) {
        for (visitor in visitors) {
            visitor.afterChildVisit(xmlNode)
        }
    }

    private fun afterDocument() {
        for (visitor in visitors) {
            visitor.afterDocument()
        }
    }

}