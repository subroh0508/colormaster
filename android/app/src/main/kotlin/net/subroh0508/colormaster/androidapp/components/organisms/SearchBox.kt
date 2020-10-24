package net.subroh0508.colormaster.androidapp.components.organisms

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.subroh0508.colormaster.androidapp.R
import net.subroh0508.colormaster.androidapp.components.atoms.Checkbox
import net.subroh0508.colormaster.androidapp.components.atoms.Chip
import net.subroh0508.colormaster.androidapp.themes.ColorMasterTheme
import net.subroh0508.colormaster.androidapp.themes.lightBackground
import net.subroh0508.colormaster.model.Brands
import net.subroh0508.colormaster.model.Types
import net.subroh0508.colormaster.presentation.search.model.SearchParams

@Composable
@ExperimentalLayout
fun SearchBox(
    params: SearchParams,
    onParamsChange: (SearchParams) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(Modifier.fillMaxWidth() + modifier) {

        BrandChips(
            params.brands,
            onChipSelected = { brand -> onParamsChange(params.change(brand)) },
        )
        Spacer(Modifier.preferredHeight(16.dp))
        TypeChips(
            params.brands,
            params.types,
            onTypeChecked = { type, checked -> onParamsChange(params.change(type, checked)) },
        )
    }
}

@Composable
@ExperimentalLayout
private fun BrandChips(
        selectedBrand: Brands?,
        onChipSelected: (Brands?) -> Unit,
) {
    Text(
        stringResource(R.string.search_box_label_brands),
        style = MaterialTheme.typography.subtitle1,
    )
    Spacer(Modifier.preferredHeight(16.dp))
    FlowRow(
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 8.dp,
    ) {
        Brands.values().forEach { brand ->
            val selected = selectedBrand == brand

            Chip(
                brand.displayName,
                selected = selected,
                onClick = { onChipSelected(if (selected) null else brand) }
            )
        }
    }
}

@Composable
@ExperimentalLayout
private fun TypeChips(
    selectedBrand: Brands?,
    selectedTypes: Set<Types>,
    onTypeChecked: (Types, Boolean) -> Unit,
) {
    val types = when (selectedBrand) {
        Brands._765, Brands._ML -> Types.MILLION_LIVE.values()
        Brands._CG -> Types.CINDERELLA_GIRLS.values()
        Brands._315 -> Types.SIDE_M.values()
        else -> arrayOf()
    }

    if (types.isEmpty()) {
        return
    }

    Text(
        stringResource(R.string.search_box_label_type),
        style = MaterialTheme.typography.subtitle1,
    )
    Spacer(Modifier.preferredHeight(16.dp))
    FlowRow {
        types.forEachIndexed { i, type ->
            Checkbox(
                label = type.label(),
                checked = selectedTypes.contains(type),
                onCheckedChange = { checked -> onTypeChecked(type, checked) },
                modifier = Modifier.padding(
                    start = if (i == 0) 0.dp else 9.dp,
                    end = 9.dp,
                ),
            )
        }
    }
}

@Composable
private fun Types.label(): String = when (this) {
    Types.MILLION_LIVE.PRINCESS -> stringResource(R.string.type_million_princess)
    Types.MILLION_LIVE.FAIRY -> stringResource(R.string.type_million_fairy)
    Types.MILLION_LIVE.ANGEL -> stringResource(R.string.type_million_angel)
    Types.CINDERELLA_GIRLS.CU -> stringResource(R.string.type_cinderella_cu)
    Types.CINDERELLA_GIRLS.CO -> stringResource(R.string.type_cinderella_co)
    Types.CINDERELLA_GIRLS.PA -> stringResource(R.string.type_cinderella_pa)
    Types.SIDE_M.MENTAL -> stringResource(R.string.type_side_m_mental)
    Types.SIDE_M.INTELLIGENT -> stringResource(R.string.type_side_m_intelligent)
    Types.SIDE_M.PHYSICAL -> stringResource(R.string.type_side_m_physical)
    else -> ""
}

@Preview
@Composable
@ExperimentalLayout
fun SearchBoxPreview_Light() {
    val (params, setParams) = remember {
        mutableStateOf(
            SearchParams(
                null,
                Brands._ML,
                setOf(Types.MILLION_LIVE.PRINCESS, Types.MILLION_LIVE.FAIRY),
                null,
            )
        )
    }

    ColorMasterTheme(darkTheme = false) {
        Row(Modifier.background(color = lightBackground)) {
            SearchBox(params, onParamsChange = { setParams(it) })
        }
    }
}
