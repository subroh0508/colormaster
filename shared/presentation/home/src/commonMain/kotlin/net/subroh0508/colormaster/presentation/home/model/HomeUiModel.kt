package net.subroh0508.colormaster.presentation.home.model

import net.subroh0508.colormaster.model.authentication.CurrentUser

interface HomeUiModel {
    val currentUser: CurrentUser?
}
