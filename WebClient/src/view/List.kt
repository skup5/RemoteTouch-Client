package view

import react.*
import react.dom.code
import styled.css
import styled.styledP
import kotlin.collections.List

class SimpleList(props: SimpleListProps) : RComponent<SimpleListProps, RState>(props) {

    override fun RBuilder.render() {
        props.items.forEach {
            styledP {
                css {

                }

                code { +it.toString() }
            }
        }
    }

}

interface SimpleListProps : RProps {
    var items: List<Any>
}

fun RBuilder.simpleList(handler: SimpleListProps.() -> Unit): ReactElement {
    return child(SimpleList::class) {
        this.attrs(handler)
    }
}