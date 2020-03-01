package net.subroh0508.colormaster.idol.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.subroh0508.colormaster.idol.R

class MainIdolsFragment : Fragment(R.layout.fragment_main_idols) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        setupIdolsFragment()
    }

    private fun setupIdolsFragment() {
        val fragment = IdolsFragment()

        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_idols, fragment)
            .commit()
    }
}
