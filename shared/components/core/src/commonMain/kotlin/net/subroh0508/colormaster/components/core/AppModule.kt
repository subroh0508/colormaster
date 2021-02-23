package net.subroh0508.colormaster.components.core

import net.subroh0508.colormaster.api.imasparql.di.Api
import net.subroh0508.colormaster.db.di.IdolColorsDatabases
import net.subroh0508.colormaster.repository.di.IdolColorsRepositories
import net.subroh0508.colormaster.repository.di.LiveRepositories

val AppModule get() = Api.Module() +
        IdolColorsRepositories.Module + IdolColorsDatabases.Module +
        LiveRepositories.Module
