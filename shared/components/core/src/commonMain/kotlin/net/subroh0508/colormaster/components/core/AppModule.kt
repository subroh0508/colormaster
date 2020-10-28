package net.subroh0508.colormaster.components.core

import net.subroh0508.colormaster.api.di.Api
import net.subroh0508.colormaster.db.di.IdolColorsDatabases
import net.subroh0508.colormaster.repository.di.IdolColorsRepositories

val AppModule get() = Api.Module + IdolColorsRepositories.Module + IdolColorsDatabases.Module
