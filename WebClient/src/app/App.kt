package app

import react.*
import react.dom.h1

/**
 *
 * @author Roman Zelenik
 */
class App : RComponent<RProps, RState>() {

    override fun RBuilder.render() {
        h1 { +"React Kotlin Template" }
    }
}

fun RBuilder.app(): ReactElement {
    return child(App::class) {}
}