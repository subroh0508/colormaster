package net.subroh0508.colormaster.repository.di

import net.subroh0508.colormaster.network.authentication.di.Authentication
import net.subroh0508.colormaster.network.firestore.di.Firestore
import net.subroh0508.colormaster.network.imasparql.di.Api

val DataModule get() = Api.Module() + Authentication.Module + Firestore.Module +
        AuthenticationRepositories.Module +
        IdolColorsRepositories.Module +
        LiveRepositories.Module
