package net.subroh0508.colormaster.androidapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.databinding.DataBindingUtil
import androidx.ui.tooling.preview.Preview
import net.subroh0508.colormaster.androidapp.databinding.ActivityMainBinding
import net.subroh0508.colormaster.androidapp.ui.HelloWorldJetpackComposeTheme

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HelloWorldJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainDrawer()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun MainDrawer() {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        topBar = { TopBar(scaffoldState) },
        drawerContent = { DrawerContent() },
        bodyContent = { Text("Body Content") },
        scaffoldState = scaffoldState
    )
}

@Composable
fun TopBar(scaffoldState: ScaffoldState) {
    TopAppBar {
        IconButton(
            onClick = { scaffoldState.drawerState.open {  } },
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(
                painter = VectorPainter(asset = Icons.Default.Menu),
                modifier = Modifier.size(Dp(24F))
                    .align(Alignment.CenterVertically)
            )
        }
        Spacer(Modifier.preferredWidth(Dp(16F)))
        Text(
            text = stringResource(R.string.app_name),
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.CenterVertically),
            style = MaterialTheme.typography.h6
        )
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
