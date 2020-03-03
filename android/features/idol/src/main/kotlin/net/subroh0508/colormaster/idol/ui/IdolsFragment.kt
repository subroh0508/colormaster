package net.subroh0508.colormaster.idol.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.ChipGroup
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import net.subroh0508.colormaster.idol.R
import net.subroh0508.colormaster.idol.databinding.FragmentIdolsBinding
import net.subroh0508.colormaster.idol.ui.viewmodel.IdolsViewModel
import net.subroh0508.colormaster.model.Titles
import net.subroh0508.colormaster.model.Types
import net.subroh0508.colormaster.widget.ui.FilterChip
import net.subroh0508.colormaster.widget.ui.onCheckedChanged
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.android.subKodein
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider

class IdolsFragment : Fragment(R.layout.fragment_idols), KodeinAware {
    private val idolsViewModelProvider: () -> IdolsViewModel by provider()
    private val idolsViewModel by activityViewModels<IdolsViewModel> {
        object : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = idolsViewModelProvider() as T
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        val binding = FragmentIdolsBinding.bind(view)
        val idolsSheetBehavior = BottomSheetBehavior.from(binding.fragmentBottomSheetIdols)
        initBottomSheetShapeAppearance(binding)
        idolsSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        setupIdolsFragment()

        idolsViewModel.uiModel.observe(viewLifecycleOwner) { (_, _, _, filters) ->
            binding.imasTitleFilters.setupFilter(
                allFilterSet = Titles.values().toSet(),
                currentFilterSet = filters.title?.let(::setOf) ?: setOf(),
                filterName = Titles::displayName
            ) { checked, title -> idolsViewModel.filterChanged(title, checked) }
            binding.attributesFilters.setupFilter(
                allFilterSet = filters.allTypes,
                currentFilterSet = filters.types.toSet(),
                filterName = { "" }
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

    override val kodein by subKodein(kodein()) {
        bind<IdolsViewModel>() with provider { IdolsViewModel(instance()) }
    }
    override val kodeinTrigger = KodeinTrigger()
}
