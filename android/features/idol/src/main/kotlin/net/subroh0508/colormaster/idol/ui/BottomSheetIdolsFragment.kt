package net.subroh0508.colormaster.idol.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import net.subroh0508.colormaster.idol.R
import net.subroh0508.colormaster.idol.databinding.FragmentBottomSheetIdolsBinding
import net.subroh0508.colormaster.idol.ui.adapter.IdolsAdapter
import net.subroh0508.colormaster.idol.ui.viewmodel.IdolsViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.android.subKodein
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.*

class BottomSheetIdolsFragment : Fragment(R.layout.fragment_bottom_sheet_idols), KodeinAware {
    private val idolsViewModelProvider: () -> IdolsViewModel by provider()
    private val idolsViewModel by activityViewModels<IdolsViewModel> {
        object : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = idolsViewModelProvider() as T
        }
    }

    private val linearLayoutManager: LinearLayoutManager by lazy { LinearLayoutManager(context) }
    private val adapter: IdolsAdapter by lazy { IdolsAdapter(requireContext(), idolsViewModel) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        kodeinTrigger.trigger()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentBottomSheetIdolsBinding.bind(view)
        binding.idolList.layoutManager = linearLayoutManager
        binding.idolList.adapter = adapter
        binding.isFiltered = false

        idolsViewModel.uiModel.observe(viewLifecycleOwner) { adapter.notifyDataSetChanged() }
        idolsViewModel.loadItems()
    }

    override val kodein by subKodein(kodein()) {
        bind<IdolsViewModel>() with provider { IdolsViewModel(instance()) }
    }
    override val kodeinTrigger = KodeinTrigger()
}
