package net.subroh0508.colormaster.repository.di

import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.repository.internal.IdolColorsRepositoryImpl
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

object IdolColorsRepositories {
    val Module get() = Kodein.Module(name = "IdolColorRepository") {
        bind<IdolColorsRepository>() with singleton { IdolColorsRepositoryImpl(instance()) }
    }
}
