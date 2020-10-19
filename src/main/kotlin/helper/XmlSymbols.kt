package helper

enum class XmlSymbols(val value: Char) {
    OPEN_TAG('<'),
    END_TAG('>'),
    SLASH('/'),
    PROLOG('?'),
    COMMENT('!'),

//    CLOSE_TAG("</"),
//    INLINE_CLOSE_TAG("/>")
}