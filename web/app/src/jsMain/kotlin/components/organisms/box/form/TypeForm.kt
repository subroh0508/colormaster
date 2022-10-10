package components.organisms.box.form

import androidx.compose.runtime.Composable
import components.atoms.checkbox.CheckBoxGroup
import material.components.TypographySubtitle1
import net.subroh0508.colormaster.components.core.external.I18nextText
import net.subroh0508.colormaster.components.core.external.invoke
import net.subroh0508.colormaster.components.core.ui.LocalI18n
import net.subroh0508.colormaster.model.Brands
import net.subroh0508.colormaster.model.Types
import org.jetbrains.compose.web.css.marginLeft
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text

private const val TYPE_ID_PREFIX = "type"

@Composable
fun TypeForm(
    brands: Brands?,
    selections: Set<Types>,
    onChange: (Types, Boolean) -> Unit,
) {
    val t = LocalI18n() ?: return
    val types = brands?.getTypes(t) ?: return

    TypographySubtitle1(applyAttrs = {
        classes("mdc-theme-text-primary")
        style { padding(8.px, 16.px) }
    }) { Text(t("searchBox.attributes.type.label")) }

    CheckBoxGroup(
        types,
        selections.map { LabeledType(it, t) },
        {
            style {
                padding(8.px, 16.px)
                marginLeft((-11).px)
            }
        },
    ) { item, checked -> onChange(item.type, checked) }
}

private data class LabeledType(
    val type: Types,
    val label: String,
) {
    constructor(type: Types, t: I18nextText) : this(type, t(type.labelKey))

    override fun toString() = label
}

private fun Brands.getTypes(t: I18nextText) = when (this) {
    Brands._765, Brands._ML -> Types.MillionLive.values()
    Brands._CG -> Types.CinderellaGirls.values()
    Brands._315 -> Types.SideM.values()
    else -> null
}?.map { it.name.lowercase() to LabeledType(it, t) }

private val Types.labelKey get() = "searchBox.attributes.type." + when (this) {
    is Types.CinderellaGirls -> name
    is Types.MillionLive -> name
    is Types.SideM -> name
    else -> ""
}.lowercase()
