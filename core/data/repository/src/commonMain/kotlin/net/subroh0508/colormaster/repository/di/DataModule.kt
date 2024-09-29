package net.subroh0508.colormaster.repository.di

import net.subroh0508.colormaster.api.authentication.di.Authentication
import net.subroh0508.colormaster.api.firestore.di.Firestore
import net.subroh0508.colormaster.api.imasparql.di.Api

val DataModule get() = Api.Module() + Authentication.Module + Firestore.Module +
        AuthenticationRepositories.Module +
        IdolColorsRepositories.Module +
        LiveRepositories.Module
