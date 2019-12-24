package lib.materialui.core

import lib.materialui.MProps
import react.*

@JsModule("@material-ui/core/Button")
private external val buttonModule: dynamic

@Suppress("UnsafeCastFromDynamic")
private val buttonComponent: RClass<MButtonProps> = buttonModule.default

fun RBuilder.mButton(handler: RHandler<MButtonProps>): ReactElement {
    return child(buttonComponent, js("{}"), handler)
}

interface MButtonProps : MProps {
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