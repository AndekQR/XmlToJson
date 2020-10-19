package service

import helper.XmlTagType

class ActualType {
    private var actualType = XmlTagType.UNMACHED

    fun set(tag: XmlTagType) {
        this.actualType = tag
    }

    fun isTag(tag: XmlTagType): Boolean {
        if (tag == actualType) return true
        return false
    }

    fun get(): XmlTagType {
        return this.actualType
    }
}