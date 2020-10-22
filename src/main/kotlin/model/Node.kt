package model

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

data class Node(
        var parent: Optional<Node> = Optional.empty(), //pierwszy węzeł bedzie miał rodzica nulla
        val childs: ArrayList<Node> = ArrayList(),
        val atributes: HashMap<String, String> = HashMap(),
        var name: String = "",
        var value: Optional<String> = Optional.empty(),
        var comment: Optional<String> = Optional.empty()
) {
    override fun toString(): String {
        return name + ": " + if(value.isPresent) value.get() else ""
    }
}