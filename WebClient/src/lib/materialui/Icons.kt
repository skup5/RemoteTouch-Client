package lib.materialui

import kotlinext.js.jsObject
import react.*

@JsModule("@material-ui/icons")
private external val iconModule: dynamic

@Suppress("UnsafeCastFromDynamic")
private val recentIconComponent: RClass<MIconProps> = iconModule.Restore

fun RBuilder.mNotificationsIcon(props: MIconProps = jsObject { }): ReactElement = createElement(iconModule.Notifications as Any, props)
fun RBuilder.mMessageIcon(props: MIconProps = jsObject { }): ReactElement = createElement(iconModule.Textsms as Any, props)
fun RBuilder.mPhoneIcon(props: MIconProps = jsObject { }): ReactElement = createElement(iconModule.Phone as Any, props)

interface MIconProps : MProps {

}

enum class MIconColor {
    inherit, primary, secondary, action, error, disabled
}