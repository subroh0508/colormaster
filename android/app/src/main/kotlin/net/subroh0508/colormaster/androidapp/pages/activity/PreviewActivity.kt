package net.subroh0508.colormaster.androidapp.pages.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.setContent
import net.subroh0508.colormaster.androidapp.pages.Preview
import net.subroh0508.colormaster.androidapp.previewIdolIds
import net.subroh0508.colormaster.presentation.preview.viewmodel.PreviewViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PreviewActivity : AppCompatActivity() {
    private val viewModel: PreviewViewModel by viewModel()

    @ExperimentalMaterialApi
    @ExperimentalLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { Preview(viewModel) }
    }

    override fun onResume() {
        super.onResume()

        viewModel.fetch(intent.previewIdolIds)
    }
}
