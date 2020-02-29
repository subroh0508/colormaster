package net.subroh0508.colormaster.corecomponent

import org.kodein.di.Kodein
import net.subroh0508.colormaster.api.di.Api
import net.subroh0508.colormaster.repository.di.IdolColorsRepositories

val AppModule = Kodein.Module(name = "AppModule") {
    import(Api.Module)
    import(IdolColorsRepositories.Module)
}
