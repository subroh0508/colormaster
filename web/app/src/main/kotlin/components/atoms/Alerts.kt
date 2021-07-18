package components.atoms

import kotlinx.css.*
import kotlinx.html.DIV
import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import kotlinx.html.title
import materialui.components.button.enums.ButtonColor
import materialui.components.icon.enums.IconFontSize
import materialui.components.icon.icon
import materialui.components.iconbutton.iconButton
import materialui.components.paper.paper
import materialui.components.typography.typography
import materialui.styles.makeStyles
import materialui.styles.palette.PaletteType
import materialui.styles.palette.main
import materialui.styles.palette.type
import materialui.styles.typography.fontWeightMedium
import materialui.styles.typography.typography
import react.*
import react.dom.attrs
import react.dom.div

fun RBuilder.successAlert(handler: RHandler<AlertsProps>) = child(alertComponent) { handler(); attrs.type = AlertsType.success }
fun RBuilder.infoAlert(handler: RHandler<AlertsProps>) = child(alertComponent) { handler(); attrs.type = AlertsType.info }
fun RBuilder.warningAlert(handler: RHandler<AlertsProps>) = child(alertComponent) { handler(); attrs.type = AlertsType.warning }
fun RBuilder.errorAlert(handler: RHandler<AlertsProps>) = child(alertComponent) { handler(); attrs.type = AlertsType.error }

private val alertComponent = functionComponent<AlertsProps> { props ->
    val classes = useStyles()

    val alertsClassName = when (props.type) {
        AlertsType.success -> classes.success
        AlertsType.info -> classes.info
        AlertsType.warning -> classes.warning
        AlertsType.error -> classes.error
        else -> ""
    }

    val icon = buildElement {
        when (props.type) {
            AlertsType.success -> icon { attrs.fontSize = IconFontSize.inherit; +"check_circle_outline_outlined_icon" }
            AlertsType.info -> icon { attrs.fontSize = IconFontSize.inherit; +"info_outlined_icon" }
            AlertsType.warning -> icon { attrs.fontSize = IconFontSize.inherit; +"report_problem_outlined_icon" }
            AlertsType.error -> icon { attrs.fontSize = IconFontSize.inherit; +"error_outline_outlined_icon" }
        }
    }

    paper {
        attrs {
            className = "${classes.root} $alertsClassName"
            role = "alert"
            square = true
            elevation = 0
        }

        div(classes.icon) { child(icon) }
        div(classes.message) {
            props.title?.let { title -> alertTitle { +title } }

            +props.message
        }

        if (props.onClose != null) {
            div(classes.action) {
                iconButton {
                    attrs["size"] = "small"
                    attrs["aria-label"] = "Close"
                    attrs.title = "Close"
                    attrs.color = ButtonColor.inherit
                    attrs.onClickFunction = { _ -> props.onClose }

                    icon { +"close_icon" }
                }
            }
        }
    }
}

private fun RBuilder.alertTitle(handler: RHandler<RProps>) = child(alertTitleComponent, handler = handler)

private val alertTitleComponent = functionComponent<RProps> { props ->
    val classes = useTitleStyles()

    typography(factory = { DIV(mapOf(), it) }) {
        attrs.gutterBottom = true
        attrs.className = classes.root

        props.children()
    }
}

@Suppress("EnumEntryName")
enum class AlertsType {
    success, info, warning, error
}

external interface AlertsProps : RProps {
    var type: AlertsType?
    var title: String?
    var message: String
    var onClose: (() -> Unit)?
}

private external interface AlertsStyle {
    val root: String
    val success: String
    val info: String
    val warning: String
    val error: String
    val icon: String
    val message: String
    val action: String
}

private external interface AlertsTitleStyle {
    val root: String
}

private val useStyles = makeStyles<AlertsStyle> {
    val getColor: (Color, Int) -> Color = { color, percent ->
        when (theme.palette.type) {
            PaletteType.dark -> color.lighten(percent)
            PaletteType.light -> color.darken(percent)
        }
    }

    val getBackgroundColor: (Color, Int) -> Color = { color, percent ->
        when (theme.palette.type) {
            PaletteType.light -> color.lighten(percent)
            PaletteType.dark -> color.darken(percent)
        }
    }

    "root" {
        typography(theme.typography.body2)
        borderRadius = theme.shape.borderRadius.px
        backgroundColor = Color.transparent
        display = Display.flex
        padding(6.px, 16.px)
        margin(0.px, 0.px)
    }
    "success" {
        color = getColor(theme.palette.success.main, 60)
        backgroundColor = getBackgroundColor(theme.palette.success.main, 90)
        descendants("\$icon") {
            color = theme.palette.success.main
        }
    }
    "info" {
        color = getColor(theme.palette.info.main, 60)
        backgroundColor = getBackgroundColor(theme.palette.info.main, if (theme.palette.type == PaletteType.dark) 90 else 75)
        descendants("\$icon") {
            color = theme.palette.info.main
        }
    }
    "warning" {
        color = getColor(theme.palette.warning.main, 60)
        backgroundColor = getBackgroundColor(theme.palette.warning.main, 90)
    }
    "error" {
        color = getColor(theme.palette.error.main, 60)
        backgroundColor = getBackgroundColor(theme.palette.error.main, 90)
    }
    "icon" {
        marginRight = 12.px
        padding(7.px, 0.px)
        display = Display.flex
        fontSize = 22.px
        opacity = 0.9
    }
    "message" {
        padding(8.px, 0.px)
    }
    "action" {
        display = Display.flex
        alignItems = Align.center
        marginLeft = LinearDimension.auto
        paddingLeft = 16.px
        marginRight = (-8).px
    }
}

private val useTitleStyles = makeStyles<AlertsTitleStyle> {
    "root" {
        fontWeight = theme.typography.fontWeightMedium
        marginTop = (-2).px
    }
}
