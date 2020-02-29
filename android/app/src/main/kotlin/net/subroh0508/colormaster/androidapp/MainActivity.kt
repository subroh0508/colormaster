package net.subroh0508.colormaster.androidapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import net.subroh0508.colormaster.androidapp.R
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.android.kodein
import org.kodein.di.android.subKodein
import org.kodein.di.bindings.WeakContextScope
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.scoped
import org.kodein.di.generic.singleton
import org.kodein.di.subKodein

class MainActivity : AppCompatActivity(R.layout.activity_main), KodeinAware {
    private val viewModel: MainViewModel by instance()

    private val linearLayoutManager: LinearLayoutManager by lazy { LinearLayoutManager(this) }
    private val adapter: IdolColorAdapter by lazy {
        IdolColorAdapter(this, viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        kodeinTrigger.trigger()

        super.onCreate(savedInstanceState)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        viewModel.loadingState.observe(this) { adapter.notifyDataSetChanged() }
        viewModel.loadItems()
    }

    override val kodein by subKodein(kodein()) {
        bind<MainViewModel.Factory>() with scoped(WeakContextScope.of<MainActivity>()).singleton { MainViewModel.Factory(instance()) }
        bind<MainViewModel>() with scoped(WeakContextScope.of<MainActivity>()).singleton { ViewModelProvider(this@MainActivity, instance())[MainViewModel::class.java] }
    }
    override val kodeinTrigger = KodeinTrigger()
}
