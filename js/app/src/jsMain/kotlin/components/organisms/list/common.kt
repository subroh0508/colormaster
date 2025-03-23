package components.organisms.list

const val GRID_MIN_WIDTH = 216
const val GRID_MARGIN_HORIZONTAL = 8

fun buildSelections(
    selections: List<String>,
    id: String,
    selected: Boolean,
) = if (selected) selections + id else selections - id
