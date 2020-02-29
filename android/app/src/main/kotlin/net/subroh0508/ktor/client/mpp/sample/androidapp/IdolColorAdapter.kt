package net.subroh0508.ktor.client.mpp.sample.androidapp

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.listitem_footer.view.*
import kotlinx.android.synthetic.main.listitem_idol_color.view.*
import net.subroh0508.ktor.client.mpp.sample.valueobject.IdolColor

class IdolColorAdapter(
    private val context: Context,
    private val viewModel: MainViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val MAIN_ITEM_VIEW = 1
        private const val FOOTER_ITEM_VIEW = -1
    }

    private val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        MAIN_ITEM_VIEW -> MainViewHolder(inflater.inflate(R.layout.listitem_idol_color, parent, false))
        FOOTER_ITEM_VIEW -> FooterViewHolder(inflater.inflate(R.layout.listitem_footer, parent, false))
        else -> throw IllegalStateException("Unknown viewType: $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MainViewHolder -> holder.bind(viewModel.items[position])
            is FooterViewHolder -> holder.bind(viewModel.loadingState.value)
        }
    }

    override fun getItemCount() = viewModel.itemCount
    override fun getItemId(position: Int) = if (position == itemCount - 1) Long.MAX_VALUE else viewModel.itemId(position)
    override fun getItemViewType(position: Int) = if (position == itemCount - 1) FOOTER_ITEM_VIEW else MAIN_ITEM_VIEW

    class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: IdolColor) = with (itemView) {
            setBackgroundColor(Color.parseColor(item.color))

            name.text = item.name
            colorCode.text = item.color

            val textColor = ContextCompat.getColor(context, if (item.isBrighter) R.color.black else R.color.white)

            name.setTextColor(textColor)
            colorCode.setTextColor(textColor)
        }
    }

    class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(state: MainViewModel.LoadingState?) = with (itemView) {
            message.visibility = when {
                state is MainViewModel.LoadingState.Loaded && state.items.isNotEmpty() -> View.GONE
                state is MainViewModel.LoadingState.Loading -> View.GONE
                else -> View.VISIBLE
            }
            progress.visibility = when (state) {
                is MainViewModel.LoadingState.Loading -> View.VISIBLE
                else -> View.GONE
            }

            message.text = when {
                state is MainViewModel.LoadingState.Loaded && state.items.isEmpty() -> context.getString(R.string.results_empty)
                state is MainViewModel.LoadingState.Error -> state.exception.localizedMessage
                else -> ""
            }
        }
    }
}
