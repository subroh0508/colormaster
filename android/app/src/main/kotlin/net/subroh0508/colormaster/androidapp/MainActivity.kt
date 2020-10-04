package net.subroh0508.colormaster.androidapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import androidx.ui.tooling.preview.Preview
import net.subroh0508.colormaster.androidapp.databinding.ActivityMainBinding
import net.subroh0508.colormaster.androidapp.themes.HelloWorldJetpackComposeTheme

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HelloWorldJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Backdrop()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@ExperimentalMaterialApi
@Composable
fun Backdrop() {
    val backdropScaffoldState = rememberBackdropScaffoldState(BackdropValue.Concealed)

    BackdropScaffold(
        scaffoldState = backdropScaffoldState,
        appBar = { TopBar(backdropScaffoldState) },
        backLayerContent = {
            Text("BackLayerContent")
        },
        frontLayerContent = {
            Text("FrontLayerContent")
        }
    )
}


@Composable
fun MainDrawer() {
    val scaffoldState = rememberScaffoldState()

    /*
    Scaffold(
        topBar = { TopBar(scaffoldState) },
        drawerContent = { DrawerContent() },
        bodyContent = { Text("Body Content") },
        scaffoldState = scaffoldState
    )
    */
}

@ExperimentalMaterialApi
@Composable
fun TopBar(backdropScaffoldState: BackdropScaffoldState) {
    Row(
        Modifier.preferredHeight(56.dp)
            .background(MaterialTheme.colors.background)
    ) {
        Image(
            asset = vectorResource(R.drawable.ic_menu),
            modifier = Modifier.align(Alignment.CenterVertically)
                .clickable(onClick = { })
        )
        Spacer(Modifier.preferredWidth(Dp(16F)))

        MainTabs(modifier = Modifier.weight(1F).align(Alignment.CenterVertically))
    }
}

@Composable
fun MainTabs(modifier: Modifier) {
    var tabSelected by remember { mutableStateOf(0) }

    ScrollableTabRow(
        selectedTabIndex = tabSelected,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onSurface,
        indicator = {},
        divider = {},
        modifier = modifier
    ) {
        stringArrayResource(R.array.main_tabs).forEachIndexed { index, title ->
            val selected = index == tabSelected

            var textModifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            if (selected) {
                textModifier =
                    Modifier.border(BorderStroke(2.dp, Color.White), RoundedCornerShape(16.dp))
                        .then(textModifier)
            }

            Tab(
                selected = selected,
                onClick = { tabSelected = index }
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.h6,
                    modifier = textModifier
                )
            }
        }
    }
}

@Composable
fun DrawerContent() {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.preferredHeight(Dp(24F)))
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(start = Dp(16F), bottom = Dp(18F))
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.preferredHeight(Dp(36F))
                    .wrapContentHeight(Alignment.Bottom)
            )
            Text(
                text = "v2020.09.20.01-beta",
                style = MaterialTheme.typography.caption,
                modifier = Modifier.preferredHeight(Dp(20F))
                    .wrapContentHeight(Alignment.Bottom)
            )
        }
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .12F))
        Text(
            text = stringResource(R.string.app_menu_search_label),
            style = MaterialTheme.typography.caption,
            modifier = Modifier.preferredHeight(Dp(28F))
                .padding(start = Dp(16F))
                .wrapContentHeight(Alignment.Bottom)
        )
        Spacer(Modifier.preferredHeight(Dp(8F)))
        DrawerButton(
            asset = Icons.Default.Search,
            label = stringResource(R.string.app_menu_search_attributes)
        ) {}
        Spacer(Modifier.preferredHeight(Dp(8F)))
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .12F))
        Text(
            text = stringResource(R.string.app_menu_about_label),
            style = MaterialTheme.typography.caption,
            modifier = Modifier.preferredHeight(Dp(28F))
                .padding(start = Dp(16F))
                .wrapContentHeight(Alignment.Bottom)
        )
        Spacer(Modifier.preferredHeight(Dp(8F)))
        DrawerButton(
            asset = Icons.Default.Search,
            label = stringResource(R.string.app_menu_about_how_to_use)
        ) {}
        Spacer(Modifier.preferredHeight(Dp(8F)))
        DrawerButton(
            asset = Icons.Default.Search,
            label = stringResource(R.string.app_menu_about_development)
        ) {}
        Spacer(Modifier.preferredHeight(Dp(8F)))
        DrawerButton(
            asset = Icons.Default.Search,
            label = stringResource(R.string.app_menu_about_terms)
        ) {}
        Spacer(Modifier.preferredHeight(Dp(8F)))
    }
}

@Composable
fun DrawerButton(
    asset: VectorAsset,
    label: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        contentColor = MaterialTheme.colors.onSurface,
        modifier = Modifier.fillMaxWidth()
            .preferredHeight(Dp(48F))
    ) {
        Spacer(Modifier.preferredWidth(Dp(16F)))
        Icon(painter = VectorPainter(asset = asset))
        Spacer(Modifier.preferredWidth(Dp(16F)))
        Text(
            text = label,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview("Scaffold")
@Composable
fun PreviewScaffold() {
    HelloWorldJetpackComposeTheme(darkTheme = true) {
        Surface { MainDrawer() }
    }
}
