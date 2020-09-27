package net.subroh0508.colormaster.idol.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
// import kotlinx.android.synthetic.main.item_footer.view.*
// import kotlinx.android.synthetic.main.item_idol_color.view.*
import net.subroh0508.colormaster.idol.R
import net.subroh0508.colormaster.idol.ui.viewmodel.IdolsViewModel
import net.subroh0508.colormaster.model.UiModel
import net.subroh0508.colormaster.model.UiModel.Search.IdolColorItem

class IdolsAdapter(
    private val context: Context,
    private val viewModel: IdolsViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val MAIN_ITEM_VIEW = 1
        private const val FOOTER_ITEM_VIEW = -1
    }

    private val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        MAIN_ITEM_VIEW -> MainViewHolder(
            inflater.inflate(
                R.layout.item_idol_color,
                parent,
                false
            )
        )
        FOOTER_ITEM_VIEW -> FooterViewHolder(
                inflater.inflate(
                    R.layout.item_footer,
                    parent,
                    false
                )
            )
        else -> throw IllegalStateException("Unknown viewType: $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MainViewHolder -> holder.bind(viewModel.items[position])
            is FooterViewHolder -> holder.bind(viewModel.uiModel.value)
        }
    }

    override fun getItemCount() = viewModel.itemCount + 1
    override fun getItemId(position: Int) = if (position == itemCount - 1) Long.MAX_VALUE else viewModel.itemId(position)
    override fun getItemViewType(position: Int) = if (position == itemCount - 1) FOOTER_ITEM_VIEW else MAIN_ITEM_VIEW

    class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: IdolColorItem) = with (itemView) {
            /*
            val (it, selected) = item

            setBackgroundColor(Color.parseColor(it.color))

            name.text = it.name
            colorCode.text = it.color

            val textColor = ContextCompat.getColor(context, if (it.isBrighter) R.color.black else R.color.white
            )

            name.setTextColor(textColor)
            colorCode.setTextColor(textColor)
            */
        }
    }

    class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(state: UiModel.Search?) = with (itemView) {
            /*
            message.visibility =
                if (state?.isLoading == false && (state.items.isEmpty() || state.error != null))
                    View.VISIBLE
                else
                    View.GONE
            progress.visibility =
                if (state?.isLoading != false) View.VISIBLE else View.GONE

            message.text = when {
                state?.isLoading == false && state.items.isEmpty() -> context.getString(
                    R.string.results_empty
                )
                state?.isLoading == false && state.error != null -> state.error?.localizedMessage
                else -> ""
            }
            */
        }
    }
}
