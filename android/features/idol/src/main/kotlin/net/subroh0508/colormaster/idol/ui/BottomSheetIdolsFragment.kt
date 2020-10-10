package net.subroh0508.colormaster.idol.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import net.subroh0508.colormaster.idol.R
import net.subroh0508.colormaster.idol.databinding.FragmentBottomSheetIdolsBinding
import net.subroh0508.colormaster.idol.ui.adapter.IdolsAdapter
import net.subroh0508.colormaster.idol.ui.viewmodel.IdolsViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BottomSheetIdolsFragment : Fragment(R.layout.fragment_bottom_sheet_idols) {
    /*
    private val idolsViewModel: IdolsViewModel by sharedViewModel()

    private val linearLayoutManager: LinearLayoutManager by lazy { LinearLayoutManager(context) }
    private val adapter: IdolsAdapter by lazy { IdolsAdapter(requireContext(), idolsViewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentBottomSheetIdolsBinding.bind(view)
        binding.idolList.layoutManager = linearLayoutManager
        binding.idolList.adapter = adapter
        binding.isFiltered = false

        idolsViewModel.uiModel.observe(viewLifecycleOwner) { adapter.notifyDataSetChanged() }
        idolsViewModel.loadItems()
    }
    */
}
