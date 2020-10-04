package net.subroh0508.colormaster.androidapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.compose.ui.platform.setContent
import androidx.databinding.DataBindingUtil
import net.subroh0508.colormaster.androidapp.databinding.ActivityMainBinding
import net.subroh0508.colormaster.androidapp.pages.Home

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { Home() }
    }
}
