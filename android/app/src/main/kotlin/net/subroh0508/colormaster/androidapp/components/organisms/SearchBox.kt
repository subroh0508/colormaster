package net.subroh0508.colormaster.androidapp.components.organisms

import androidx.compose.material.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import net.subroh0508.colormaster.androidapp.R
import net.subroh0508.colormaster.androidapp.components.atoms.Checkbox
import net.subroh0508.colormaster.androidapp.components.atoms.Chip
import net.subroh0508.colormaster.androidapp.components.atoms.FlexRow
import net.subroh0508.colormaster.androidapp.components.atoms.DebounceTextField
import net.subroh0508.colormaster.androidapp.themes.ColorMasterTheme
import net.subroh0508.colormaster.androidapp.themes.lightBackground
import net.subroh0508.colormaster.features.search.model.SearchParams
import net.subroh0508.colormaster.model.Brands
import net.subroh0508.colormaster.model.Types
import net.subroh0508.colormaster.model.toIdolName

@Composable
@ExperimentalLayoutApi
fun SearchBox(
    params: SearchParams,
    modifier: Modifier = Modifier,
    onParamsChange: (SearchParams) -> Unit = {},
) {
    when (params)  {
        is SearchParams.ByName -> Column(Modifier.fillMaxWidth().then(modifier)) {
            DebounceTextField(
                text = params.idolName?.value,
                labelRes = R.string.search_box_label_idol_name,
                onTextChanged = { name -> onParamsChange(params.change(name.toIdolName())) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            BrandChips(
                params.brands,
                onChipSelected = { brand -> onParamsChange(params.change(brand)) },
            )
            Spacer(Modifier.height(16.dp))
            TypeChips(
                params.brands,
                params.types,
                onTypeChecked = { type, checked -> onParamsChange(params.change(type, checked)) },
            )
        }
        else -> Unit
    }
}

@Composable
@ExperimentalLayoutApi
private fun BrandChips(
        selectedBrand: Brands?,
        onChipSelected: (Brands?) -> Unit,
) {
    Text(
        stringResource(R.string.search_box_label_brands),
        style = MaterialTheme.typography.subtitle1,
    )
    Spacer(Modifier.height(16.dp))
    FlexRow(
        spacing = 8.dp,
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
@ExperimentalLayoutApi
private fun TypeChips(
    selectedBrand: Brands?,
    selectedTypes: Set<Types>,
    onTypeChecked: (Types, Boolean) -> Unit,
) {
    val types = when (selectedBrand) {
        Brands._765, Brands._ML -> Types.MillionLive.values()
        Brands._CG -> Types.CinderellaGirls.values()
        Brands._315 -> Types.SideM.values()
        else -> arrayOf()
    }

    if (types.isEmpty()) {
        return
    }

    Text(
        stringResource(R.string.search_box_label_type),
        style = MaterialTheme.typography.subtitle1,
    )
    Spacer(Modifier.height(16.dp))
    FlexRow {
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
    Types.MillionLive.PRINCESS -> stringResource(R.string.type_million_princess)
    Types.MillionLive.FAIRY -> stringResource(R.string.type_million_fairy)
    Types.MillionLive.ANGEL -> stringResource(R.string.type_million_angel)
    Types.CinderellaGirls.CU -> stringResource(R.string.type_cinderella_cu)
    Types.CinderellaGirls.CO -> stringResource(R.string.type_cinderella_co)
    Types.CinderellaGirls.PA -> stringResource(R.string.type_cinderella_pa)
    Types.SideM.MENTAL -> stringResource(R.string.type_side_m_mental)
    Types.SideM.INTELLIGENT -> stringResource(R.string.type_side_m_intelligent)
    Types.SideM.PHYSICAL -> stringResource(R.string.type_side_m_physical)
    else -> ""
}

@Preview
@Composable
@ExperimentalLayoutApi
fun SearchBoxPreview_Light() {
    val (params, setParams) = remember {
        mutableStateOf(
            SearchParams.ByName(
                null,
                Brands._ML,
                setOf(Types.MillionLive.PRINCESS, Types.MillionLive.FAIRY),
                null,
            ) as SearchParams
        )
    }

    ColorMasterTheme(darkTheme = false) {
        Row(Modifier.background(color = lightBackground)) {
            SearchBox(params, onParamsChange = { setParams(it) })
        }
    }
}
