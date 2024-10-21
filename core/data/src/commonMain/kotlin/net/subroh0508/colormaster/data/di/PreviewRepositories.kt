package net.subroh0508.colormaster.data.di

import net.subroh0508.colormaster.data.DefaultPreviewRepository
import net.subroh0508.colormaster.model.PreviewRepository
import org.koin.dsl.module

object PreviewRepositories {
    val Module get() = module {
        single<PreviewRepository> { DefaultPreviewRepository(get()) }
    }
}
