package service

import helper.XmlSymbols
import helper.XmlTagType

class TagTypeDetector(firstCharOfXml: Char) {

    //typ pokazywany 'na zewnątrz klasy'
    private var typeToShow: XmlTagType = XmlTagType.UNMACHED

    private val firstCharOfTagNameRegex = Regex("[A-Z]|[a-z]|_")
    private val restChrsOfTagNameRegex = Regex("[A-Z]|[a-z]|\\d|_|.|-")

    //sprawdzone elementy
    private var checked = ArrayList<Char>()

    /**
     * typ aktulanego przeglądanego tagu, zmienna pomocnicza
     * różni się od typeToShow tym że dla znaków np. '=', '"' nie jest ustawiana na UNMACHED
     * z koleji zmienna typeToShow jest ustawiany na UNMACHED
     */
    val actualTag = ActualType()

    // pomoga gdy jest wiele atrybutów
    private var inAttributeValue = false

    init {
        actualTag.set(XmlTagType.UNMACHED)
        this.checked.add(firstCharOfXml)
    }


    fun check(char: Char) {
        if (typeToShow == XmlTagType.UNMACHED) {
            when {
                isProlog(char) -> actualTag.set(XmlTagType.PROLOG)
                isTagName(char) -> actualTag.set(XmlTagType.TAG_NAME)
                isComment(char) -> actualTag.set(XmlTagType.COMMENT)
                isTagValue(char) -> actualTag.set(XmlTagType.TAG_VALUE)
                isCloseTag(char) -> actualTag.set(XmlTagType.CLOSE_TAG)
            }
        }

        when (this.actualTag.get()) {
            //gdy wejdziemy do prologu to czytamy tutaj znaki aż do zakończenia prologu
            XmlTagType.PROLOG -> {
                setTypeToShow(XmlTagType.PROLOG, char)
                if (isPrologEnd(char)) {
                    actualTag.set(XmlTagType.UNMACHED)
                }
            }
            //gdy wejdziemy w tag, możemy mieć jego atrybut lub może ssię konczyć 'inline'
            XmlTagType.TAG_NAME -> {
                setTypeToShow(XmlTagType.TAG_NAME, char)
                if (isAttributeName(char)) {
                    actualTag.set(XmlTagType.ATTRIBUTE_NAME)
                    setTypeToShow(XmlTagType.ATTRIBUTE_NAME, char)
                }
                if (isInlineClose(char)) {
                    setTypeToShow(XmlTagType.INLINE_CLOSE_TAG, char)
                }
            }
            //gdy jesteśmy w atrybucie tagu, to po nim wystąpu wartość atrybutu
            XmlTagType.ATTRIBUTE_NAME -> {
                setTypeToShow(XmlTagType.ATTRIBUTE_NAME, char)
                if (isAttributeValue(char)) {
                    inAttributeValue = true
                    actualTag.set(XmlTagType.ATTRIBUTE_VALUE)
                    setTypeToShow(XmlTagType.ATTRIBUTE_VALUE, char)
                }
            }
            //gdy jesteśmy w wartości atrybutu to po nim może wystąpić astępny atrybut
            //wyjście z tego tagu relizowane jest przez special conditions
            XmlTagType.ATTRIBUTE_VALUE -> {
                setTypeToShow(XmlTagType.ATTRIBUTE_VALUE, char)
                if (inAttributeValue && char == '"')
                    inAttributeValue = false
                if (isAttributeName(char) && !inAttributeValue) {
                    actualTag.set(XmlTagType.ATTRIBUTE_NAME)
                    setTypeToShow(XmlTagType.ATTRIBUTE_NAME, char)
                }
            }

            XmlTagType.TAG_VALUE -> {
                setTypeToShow(XmlTagType.TAG_VALUE, char)
                if (isCloseTag(char)) {
                    actualTag.set(XmlTagType.CLOSE_TAG)
                    setTypeToShow(XmlTagType.CLOSE_TAG, char)
                }
            }
            XmlTagType.CLOSE_TAG -> {
                setTypeToShow(XmlTagType.CLOSE_TAG, char)
            }

            XmlTagType.COMMENT -> {
                setTypeToShow(XmlTagType.COMMENT, char)
                if (isCommentClose(char)) {
                    actualTag.set(XmlTagType.UNMACHED)
                    setTypeToShow(XmlTagType.UNMACHED, char)
                }
            }

        }

        this.checked.add(char)
    }


    private fun isItXmlSymbol(char: Char): Boolean {
        when (char) {
            XmlSymbols.END_TAG.value,
            XmlSymbols.OPEN_TAG.value,
            XmlSymbols.SLASH.value,
            XmlSymbols.PROLOG.value,
            XmlSymbols.COMMENT.value -> return true
        }
        return false
    }

    private fun setTypeToShow(type: XmlTagType, char: Char) {
        // special conditions
        if ((char == '=' || char == '"')) this.typeToShow = XmlTagType.UNMACHED
        else if (actualTag.isTag(XmlTagType.TAG_NAME) && (char == XmlSymbols.SLASH.value)) this.typeToShow =
            XmlTagType.INLINE_CLOSE_TAG
        else if (actualTag.isTag(XmlTagType.COMMENT) && char == '-') this.typeToShow = XmlTagType.UNMACHED
        else if (this.typeToShow == XmlTagType.TAG_NAME && char == ' ') this.typeToShow = XmlTagType.UNMACHED
        else if (typeOfCurrent() == XmlTagType.TAG_VALUE.type && char == XmlSymbols.COMMENT.value) this.typeToShow =
            XmlTagType.TAG_VALUE
        else if (isItXmlSymbol(char)) this.typeToShow = XmlTagType.UNMACHED
        else this.typeToShow = type
    }

    /**
     * jeżeli wystąpi <? to początek prologu
     */
    private fun isProlog(char: Char): Boolean {
        if (checked.last() == XmlSymbols.OPEN_TAG.value && char == XmlSymbols.PROLOG.value) {
            return true
        }
        return false
    }

    /**
     * zakończenie prologu gdy ?>
     */
    private fun isPrologEnd(char: Char): Boolean {
        return checked.last() == XmlSymbols.PROLOG.value && char == XmlSymbols.END_TAG.value
    }

    /**
     * pierwsza litera nazwy tegu musi spełniać określone warunki
     */
    private fun isTagName(char: Char): Boolean {
        if (char == ' ') return false
        val matches = firstCharOfTagNameRegex.matches(char.toString())
        if (matches && checked.last() == XmlSymbols.OPEN_TAG.value) {
            return true
        }
        return false
    }

    /**
     * Tag kończący gdy wystąpi />
     */
    private fun isCloseTag(char: Char): Boolean {
        if (char == XmlSymbols.SLASH.value && checked.last() == XmlSymbols.OPEN_TAG.value) {
            return true
        }
        return false
    }

    private fun isInlineClose(char: Char): Boolean {
        if (char == XmlSymbols.END_TAG.value && checked.last() == XmlSymbols.SLASH.value) {
            return true
        }
        return false
    }

    /**
     * komentarz gdy wystąpi <!
     */
    private fun isComment(char: Char): Boolean {
        if (checked.last() == XmlSymbols.OPEN_TAG.value && char == XmlSymbols.COMMENT.value) {
            return true
        }
        return false
    }

    /**
     * koniec komentarza gdy ->
     */
    private fun isCommentClose(char: Char): Boolean {
        return char == XmlSymbols.END_TAG.value && checked.last() == '-'
    }

    private fun isTagValue(char: Char): Boolean {
        if (char == XmlSymbols.OPEN_TAG.value || char == XmlSymbols.END_TAG.value)
            return false
        val matches = this.restChrsOfTagNameRegex.matches(char.toString())
        if (checked.last() == XmlSymbols.END_TAG.value && matches) {
            return true
        }
        return false
    }

    private fun isAttributeName(char: Char): Boolean {
        val matches = this.firstCharOfTagNameRegex.matches(char.toString())
        if (checked.last() == ' ' && matches) {
            return true
        }
        return false
    }

    private fun isAttributeValue(char: Char): Boolean {
        if (checked.last() == '"') {
            return true
        }
        return false
    }

    fun typeOfCurrent(): String {
        return this.typeToShow.type
    }


}

