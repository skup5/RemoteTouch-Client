package lib.materialui.core

import kotlinext.js.JsObject
import lib.materialui.MProps
import react.*

@JsModule("@material-ui/core/BottomNavigation")
private external val bottomNavModule: dynamic

@Suppress("UnsafeCastFromDynamic")
private val bottomNavComponent: RClass<MButtonProps> = bottomNavModule.default

fun RBuilder.mBottomNavigation(handler: RHandler<BottomNavigationProps>): ReactElement {
    return child(bottomNavComponent, js("{}"), handler)
}

 interface BottomNavigationProps : MProps {
    var value: Any
    var className: String
    var showLabels: Boolean
    var onChange: (event: JsObject, value: Any) -> Unit
}