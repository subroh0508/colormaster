package components.templates

import androidx.compose.runtime.Composable
import components.atoms.menu.MenuButton
import components.atoms.tooltip.Tooltip
import components.atoms.topappbar.TopAppActionIcon
import components.molecules.TopAppBar
import components.organisms.drawer.DrawerContent
import components.organisms.drawer.DrawerHeader
import material.components.MenuItem
import material.components.TopAppBarVariant
import material.externals.MDCDrawer
import material.externals.open
import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import net.subroh0508.colormaster.model.ui.commons.ThemeType
import page.SearchIdolPage
import utilities.*
import material.components.ModalDrawer as MaterialModalDrawer

@Composable
fun MainFrame(preference: AppPreference) {
    MaterialModalDrawer(
        headerContent = { DrawerHeader() },
        drawerContent = { DrawerContent(it) },
        mainContent = { DrawerMain(it, preference) },
    )
}

@Composable
private fun DrawerMain(
    drawer: MDCDrawer?,
    preference: AppPreference,
) = SearchIdolPage(
    TopAppBarVariant.Fixed,
    appBar = { TopAppBar(it, drawer, preference) },
)

@Composable
private fun TopAppBar(
    variant: String,
    drawer: MDCDrawer?,
    preference: AppPreference,
) = TopAppBar(
    variant,
    onClickNavigation = { drawer?.open() },
    actionContent = { DrawerActionContent(preference) },
)

@Composable
private fun DrawerActionContent(preference: AppPreference) {
    val (lang, theme, t) = LocalBrowserApp.current
    t ?: return

    MenuButton(
        "translate",
        { classes("mdc-top-app-bar__action-item") },
        icon = "translate",
        tooltip = { Tooltip(it, t("appBar.changeLanguage")) },
    ) {
        MenuItem("日本語", activated = lang == Languages.JAPANESE) {
            preference.setLanguage(Languages.JAPANESE)
        }
        MenuItem("English", activated = lang == Languages.ENGLISH) {
            preference.setLanguage(Languages.ENGLISH)
        }
    }

    TopAppActionIcon(
        theme.icon,
        { onClick { preference.setThemeType(theme.next) } },
        tooltip = { Tooltip(it, t(theme)) },
    )
}

private val ThemeType.icon get() = when (this) {
    ThemeType.DAY -> "brightness_4"
    ThemeType.NIGHT -> "brightness_7"
}

private val ThemeType.next get() = ThemeType.values()[(ordinal + 1) % 2]

private operator fun I18nextText.invoke(theme: ThemeType) = invoke(
    when (theme) {
        ThemeType.DAY -> "appBar.darkTheme"
        ThemeType.NIGHT -> "appBar.lightTheme"
    }
)