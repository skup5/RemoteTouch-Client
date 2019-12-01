package view.status

import react.*
import react.dom.span

class StatusComponent(props: StatusProps) : RComponent<StatusProps, RState>(props) {
    override fun RBuilder.render() {
        span {
            +props.content
        }
    }
}

interface StatusProps : RProps {
    var content: String
}

fun RBuilder.status(handler: StatusProps.() -> Unit): ReactElement {
    return child(StatusComponent::class) {
        this.attrs(handler)
    }
}