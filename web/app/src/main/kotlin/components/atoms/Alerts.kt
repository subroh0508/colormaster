package components.atoms

import kotlinx.css.margin
import kotlinx.css.px
import materialui.lab.components.alert.alert
import materialui.lab.components.alert.enums.AlertSeverity
import materialui.lab.components.alerttitle.alertTitle
import materialui.styles.makeStyles
import org.w3c.dom.events.Event
import react.*

fun RBuilder.successAlert(handler: RHandler<AlertProps>) = child(alertComponent) { handler(); attrs.severity = AlertSeverity.success }
fun RBuilder.infoAlert(handler: RHandler<AlertProps>) = child(alertComponent) { handler(); attrs.severity = AlertSeverity.info }
fun RBuilder.warningAlert(handler: RHandler<AlertProps>) = child(alertComponent) { handler(); attrs.severity = AlertSeverity.warning }
fun RBuilder.errorAlert(handler: RHandler<AlertProps>) = child(alertComponent) { handler(); attrs.severity = AlertSeverity.error }

private val alertComponent = functionalComponent<AlertProps> { props ->
    val classes = useStyles()

    alert {
        attrs {
            classes(classes.root)
            severity = props.severity
            onClose = if (props.onClose == null) null else { _: Event -> props.onClose?.invoke() }
        }

        props.title?.let { title -> alertTitle { +title } }

        +props.message
    }
}

external interface AlertProps : RProps {
    var severity: AlertSeverity
    var title: String?
    var message: String
    var onClose: (() -> Unit)?
}

private external interface AlertStyle {
    val root: String
}

private val useStyles = makeStyles<AlertStyle> {
    "root" {
        margin(0.px, 0.px)
    }
}
