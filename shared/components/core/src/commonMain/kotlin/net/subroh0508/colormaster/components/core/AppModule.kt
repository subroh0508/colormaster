package net.subroh0508.colormaster.components.core

import net.subroh0508.colormaster.api.authentication.di.Authentication
import net.subroh0508.colormaster.api.firestore.di.Firestore
import net.subroh0508.colormaster.api.imasparql.di.Api
import net.subroh0508.colormaster.repository.di.AuthenticationRepositories
import net.subroh0508.colormaster.repository.di.IdolColorsRepositories
import net.subroh0508.colormaster.repository.di.LiveRepositories

val AppModule get() = Api.Module() + Authentication.Module + Firestore.Module +
        AuthenticationRepositories.Module +
        IdolColorsRepositories.Module +
        LiveRepositories.Module
