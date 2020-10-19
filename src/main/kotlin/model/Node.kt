package model

data class Node(
        val parent: Node?, //pierwszy węzeł bedzie miał rodzica nulla
        val childs: ArrayList<Node>,
        val atributes: HashMap<String, String>,
        val name: String,
        val value: String
)