package components.organisms.box.form

import androidx.compose.runtime.Composable
import components.atoms.chip.ChipGroup
import material.components.TypographySubtitle1
import net.subroh0508.colormaster.model.Brands
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text
import utilities.LocalI18n
import utilities.invoke

@Composable
fun BrandForm(
    brands: Brands?,
    onChange: (Brands?) -> Unit,
) {
    val t = LocalI18n() ?: return

    TypographySubtitle1(applyAttrs = {
        classes("mdc-theme-text-primary")
        style { padding(8.px, 16.px) }
    }) { Text(t("searchBox.attributes.brands")) }

    ChipGroup(
        Brands.values().toList(),
        brands,
        { style { padding(8.px, 16.px) } },
    ) { onChange(it) }
}
