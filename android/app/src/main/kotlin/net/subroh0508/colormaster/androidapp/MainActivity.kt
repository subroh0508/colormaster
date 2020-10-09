package net.subroh0508.colormaster.androidapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.material.*
import androidx.compose.ui.platform.setContent
import androidx.databinding.DataBindingUtil
import net.subroh0508.colormaster.androidapp.databinding.ActivityMainBinding
import net.subroh0508.colormaster.androidapp.pages.Home
import net.subroh0508.colormaster.model.HexColor
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.UiModel
import net.subroh0508.colormaster.model.ui.idol.Filters

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    @ExperimentalMaterialApi
    @ExperimentalLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uiModel = UiModel.Search(
            listOf(
                UiModel.Search.IdolColorItem(IdolColor("Mitsumine_Yuika", "三峰結華", HexColor("3B91C4"))),
                UiModel.Search.IdolColorItem(IdolColor("Hachimiya_Meguru", "八宮めぐる", HexColor("FFE012"))),
                UiModel.Search.IdolColorItem(IdolColor("Higuchi_Madoka", "樋口円香",  HexColor("BE1E3E"))),
                UiModel.Search.IdolColorItem(IdolColor("Arisugawa_Natsuha", "有栖川夏葉", HexColor("90E677"))),
            ),
            Filters.Empty,
            error = null,
        )

        setContent { Home(uiModel) }
    }
}
