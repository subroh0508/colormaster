package net.subroh0508.colormaster.idol.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.ChipGroup
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import net.subroh0508.colormaster.idol.R
import net.subroh0508.colormaster.idol.databinding.FragmentIdolsBinding
import net.subroh0508.colormaster.idol.ui.viewmodel.IdolsViewModel
import net.subroh0508.colormaster.model.Brands
import net.subroh0508.colormaster.model.Types
import net.subroh0508.colormaster.model.toIdolName
import net.subroh0508.colormaster.widget.ui.FilterChip
import net.subroh0508.colormaster.widget.ui.onCheckedChanged
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class IdolsFragment : Fragment(R.layout.fragment_idols) {
    private val idolsViewModel: IdolsViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        val binding = FragmentIdolsBinding.bind(view)
        val idolsSheetBehavior = BottomSheetBehavior.from(binding.fragmentBottomSheetIdols)
        initBottomSheetShapeAppearance(binding)
        idolsSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.idolName.addTextChangedListener {
            idolsViewModel.filterChanged(it?.toString().toIdolName())
        }

        setupIdolsFragment()

        idolsViewModel.uiModel.observe(viewLifecycleOwner) { (_, filters, _, _) ->
            binding.imasTitleFilters.setupFilter(
                allFilterSet = Brands.values().toSet(),
                currentFilterSet = filters.title?.let(::setOf) ?: setOf(),
                filterName = Brands::displayName
            ) { checked, title -> idolsViewModel.filterChanged(title, checked) }
            binding.attributesFilters.setupFilter(
                allFilterSet = filters.allTypes,
                currentFilterSet = filters.types,
                filterName = { it.displayName }
            ) { checked, types -> idolsViewModel.filterChanged(types, checked) }
        }
    }

    private inline fun <reified T> ChipGroup.setupFilter(
        allFilterSet: Set<T>,
        currentFilterSet: Set<T>,
        filterName: (T) -> String,
        crossinline onCheckChanged: (Boolean, T) -> Unit
    ) {
        val shouldInflateChip = childCount == 0 || children.withIndex().any { (index, view) ->
            view.getTag(R.id.tag_filter) != allFilterSet.elementAtOrNull(index)
        }
        val filterToView = if (shouldInflateChip) {
            removeAllViews()
            allFilterSet.map { filter ->
                val chip = layoutInflater.inflate(R.layout.layout_chip, this, false) as FilterChip
                chip.onCheckedChangeListener = null
                chip.text = filterName(filter)
                chip.setTag(R.id.tag_filter, filter)
                addView(chip)
                filter to chip
            }.toMap()
        } else {
            children.map { it.getTag(R.id.tag_filter) as T to it as FilterChip }.toMap()
        }

        filterToView.forEach { (filter, chip) ->
            val shouldChecked = currentFilterSet.contains(filter)
            if (chip.isChecked != shouldChecked) {
                chip.isChecked = shouldChecked
            }
            chip.onCheckedChanged { _, checked -> onCheckChanged(checked, filter) }
        }
    }


    private fun setupIdolsFragment() {
        val fragment = BottomSheetIdolsFragment()

        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_bottom_sheet_idols, fragment)
            .disallowAddToBackStack()
            .commit()
    }

    // BottomSheetを開いた時に角丸が効かなくなるためコードで対応
    // XMLでの対応は、Backdropがコンポーネントとして提供されるまで待つ必要がありそう
    // 参考1: https://github.com/DroidKaigi/conference-app-2020/issues/104
    // 参考2: https://github.com/material-components/material-components-android/pull/437
    private fun initBottomSheetShapeAppearance(binding: FragmentIdolsBinding) {
        val shapeAppearanceModel =
            ShapeAppearanceModel.Builder()
                .setTopLeftCorner(
                    CornerFamily.ROUNDED,
                    resources.getDimension(R.dimen.bottom_sheet_corner_radius)
                )
                .setTopRightCorner(
                    CornerFamily.ROUNDED,
                    resources.getDimension(R.dimen.bottom_sheet_corner_radius)
                )
                .build()
        /**
         * FrontLayer elevation is 1dp
         * https://material.io/components/backdrop/#anatomy
         */
        val materialShapeDrawable = MaterialShapeDrawable.createWithElevationOverlay(
            requireActivity(),
            resources.getDimension(R.dimen.bottom_sheet_elevation)
        ).apply {
            setShapeAppearanceModel(shapeAppearanceModel)
        }
        binding.fragmentBottomSheetIdols.background = materialShapeDrawable
    }

    private val Types.displayName: String get() = when (this) {
        Types.CINDERELLA_GIRLS.CU -> getString(R.string.type_cinderella_cu)
        Types.CINDERELLA_GIRLS.CO -> getString(R.string.type_cinderella_co)
        Types.CINDERELLA_GIRLS.PA -> getString(R.string.type_cinderella_pa)
        Types.MILLION_LIVE.PRINCESS -> getString(R.string.type_million_princess)
        Types.MILLION_LIVE.FAIRY -> getString(R.string.type_million_fairy)
        Types.MILLION_LIVE.ANGEL -> getString(R.string.type_million_angel)
        Types.SIDE_M.PHYSICAL -> getString(R.string.type_side_m_physical)
        Types.SIDE_M.INTELLIGENT -> getString(R.string.type_side_m_intelligent)
        Types.SIDE_M.MENTAL -> getString(R.string.type_side_m_mental)
        else -> ""
    }
}
