package net.subroh0508.colormaster.androidapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import net.subroh0508.colormaster.androidapp.R

class MainActivity : AppCompatActivity(
    R.layout.activity_main
) {
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    private val linearLayoutManager: LinearLayoutManager by lazy { LinearLayoutManager(this) }
    private val adapter: IdolColorAdapter by lazy {
        IdolColorAdapter(this, viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        viewModel.loadingState.observe(this) { adapter.notifyDataSetChanged() }
        viewModel.loadItems()
    }
}
