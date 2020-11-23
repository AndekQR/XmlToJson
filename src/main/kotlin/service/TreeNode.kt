package service

import model.Node
import java.util.*

class TreeNode {

    var rootNode: Node? = null
        private set
    private var lastAddNode: Node? = null


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
}