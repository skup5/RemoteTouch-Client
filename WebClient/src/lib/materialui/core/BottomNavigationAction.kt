package lib.materialui.core

import kotlinext.js.JsObject
import lib.materialui.MProps
import react.*

@JsModule("@material-ui/core/BottomNavigationAction")
private external val bottomNavActionModule: dynamic

@Suppress("UnsafeCastFromDynamic")
private val bottomNavActionComponent: RClass<MButtonProps> = bottomNavActionModule.default

fun RBuilder.mBottomNavigationAction(handler: RHandler<BottomNavigationActionProps>): ReactElement {
    return child(bottomNavActionComponent, js("{}"), handler)
}

interface BottomNavigationActionProps : MProps {
    var value: Any
    var classes: String
    var showLabels: Boolean
    var label:Any
    var icon:ReactElement?
}