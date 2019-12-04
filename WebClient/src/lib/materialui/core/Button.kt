package lib.materialui.core

import kotlinx.html.CommonAttributeGroupFacade
import react.*

@JsModule("@material-ui/core/Button")
private external val buttonModule: dynamic

@Suppress("UnsafeCastFromDynamic")
private val buttonComponent: RClass<MButtonProps> = buttonModule.default

fun RBuilder.mButton(handler: RHandler<MButtonProps>): ReactElement {
    return child(buttonComponent, js("{}"), handler)
}

external interface MButtonProps : RProps {
    var color: String?
    var fullWidth: Boolean?
    var href: String?
    var mini: Boolean?
    var size: String?
    var variant: String?
}

@Suppress("EnumEntryName")
enum class MButtonColor {
    default, inherit, primary, secondary
}

@Suppress("EnumEntryName")
enum class MButtonVariant {
    text, outlined, contained
}

@Suppress("EnumEntryName")
enum class MButtonSize {
    small, medium, large
}