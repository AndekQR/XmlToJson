package helper

enum class XmlTagsRegex(val regex: Regex) {
    OPEN_SIMPLE_TAG(Regex("<[^?!/ :]+>")), //<kurs-java>
    CLOSE_TAG(Regex("</[^?:/<]+>")), //</kurs-java>
    OPEN_SIMPLE_PARAMETRS_TAG(Regex("<[^?!/ :]+( [A-Za-z]+=\".+\">)+")), //<artykul publikacja="2017-03-01">
    OPEN_CLOSE_TAG(Regex("<[^?!/ :]+/>")), //<MyArticle/>
    OPEN_CLOSE_PARAMETERS_TAG(Regex("<[^?!/ :]+( [A-Za-z]+=\".+\"/>)+")), //<opis status="do uzupelnienia"/>

    OPEN_NAMESPACE_TAG(Regex("<[^/<?>]+:[^<?>]+>")), //<wydawca:opis>
    INLINE_CLOSE_TAG(Regex("/>")),
    ATRIBUTE(Regex("(\\S+)=[\"']?((?:.(?![\"']?\\s+(?:\\S+)=|\\s*/?[>\"']))+.)[\"']?")),

}