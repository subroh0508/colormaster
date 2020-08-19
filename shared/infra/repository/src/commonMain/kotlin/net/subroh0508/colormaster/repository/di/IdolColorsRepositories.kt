package net.subroh0508.colormaster.repository.di

import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.repository.internal.IdolColorsRepositoryImpl
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

object IdolColorsRepositories {
    val Module get() = DI.Module(name = "IdolColorRepository") {
        bind<IdolColorsRepository>() with singleton { IdolColorsRepositoryImpl(instance(), instance()) }
    }
}
