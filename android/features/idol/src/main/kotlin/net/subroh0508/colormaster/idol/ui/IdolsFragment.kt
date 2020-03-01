package net.subroh0508.colormaster.idol.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import net.subroh0508.colormaster.idol.R
import net.subroh0508.colormaster.idol.databinding.FragmentIdolsBinding

class IdolsFragment : Fragment(R.layout.fragment_idols) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        val binding = FragmentIdolsBinding.bind(view)

        initBottomSheetShapeAppearance(binding)

        setupIdolsFragment()
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
