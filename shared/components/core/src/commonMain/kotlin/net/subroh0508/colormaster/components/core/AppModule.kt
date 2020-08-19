package net.subroh0508.colormaster.components.core

import org.kodein.di.DI
import net.subroh0508.colormaster.api.di.Api
import net.subroh0508.colormaster.repository.di.IdolColorsRepositories

val AppModule = DI.Module(name = "AppModule") {
    import(Api.Module)
    import(IdolColorsRepositories.Module)
}
