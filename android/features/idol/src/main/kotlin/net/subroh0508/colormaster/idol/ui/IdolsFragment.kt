package net.subroh0508.colormaster.idol.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import net.subroh0508.colormaster.idol.R

class IdolsFragment : Fragment(R.layout.fragment_idols) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupIdolsFragment()
    }

    private fun setupIdolsFragment() {
        val fragment = BottomSheetIdolsFragment()

        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_bottom_sheet_idols, fragment)
            .disallowAddToBackStack()
            .commit()
    }
}
