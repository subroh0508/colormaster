package net.subroh0508.colormaster.idol.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.ChipGroup
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import net.subroh0508.colormaster.idol.R
import net.subroh0508.colormaster.idol.databinding.FragmentIdolsBinding
import net.subroh0508.colormaster.widget.ui.FilterChip
import net.subroh0508.colormaster.widget.ui.onCheckedChanged

class IdolsFragment : Fragment(R.layout.fragment_idols) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        val binding = FragmentIdolsBinding.bind(view)
        val idolsSheetBehavior = BottomSheetBehavior.from(binding.fragmentBottomSheetIdols)
        initBottomSheetShapeAppearance(binding)
        idolsSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        setupIdolsFragment()

        binding.imasTitleFilters.setupFilter(
            allFilterSet = resources.getStringArray(R.array.imas_titles).toSet(),
            currentFilterSet = setOf()
        ) { _, _ -> }
        binding.attributesFilters.setupFilter(
            allFilterSet = resources.getStringArray(R.array.attributes).toSet(),
            currentFilterSet = setOf()
        ) { _, _ -> }
        binding.typeFilters.setupFilter(
            allFilterSet = resources.getStringArray(R.array.type).toSet(),
            currentFilterSet = setOf()
        ) { _, _ -> }
        binding.categoryFilters.setupFilter(
            allFilterSet = resources.getStringArray(R.array.category).toSet(),
            currentFilterSet = setOf()
        ) { _, _ -> }
        binding.divisonFilters.setupFilter(
            allFilterSet = resources.getStringArray(R.array.division).toSet(),
            currentFilterSet = setOf()
        ) { _, _ -> }
    }

    private inline fun ChipGroup.setupFilter(
        allFilterSet: Set<String>,
        currentFilterSet: Set<String>,
        crossinline onCheckChanged: (Boolean, String) -> Unit
    ) {
        val shouldInflateChip = childCount == 0 || children.withIndex().any { (index, view) ->
            view.getTag(R.id.tag_filter) != allFilterSet.elementAtOrNull(index)
        }
        val filterToView = if (shouldInflateChip) {
            removeAllViews()
            allFilterSet.map { filter ->
                val chip = layoutInflater.inflate(R.layout.layout_chip, this, false) as FilterChip
                chip.onCheckedChangeListener = null
                chip.text = filter
                chip.setTag(R.id.tag_filter, filter)
                addView(chip)
                filter to chip
            }.toMap()
        } else {
            children.map { it.getTag(R.id.tag_filter) as String to it as FilterChip }.toMap()
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

}
