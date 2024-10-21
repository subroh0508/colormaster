package net.subroh0508.colormaster.data.di

import net.subroh0508.colormaster.network.auth.di.Auth
import net.subroh0508.colormaster.network.firestore.di.Firestore
import net.subroh0508.colormaster.network.imasparql.di.Api

val DataModule get() = Api.Module() + Auth.Module + Firestore.Module +
        AuthRepositories.Module +
        IdolColorsRepositories.Module +
        LiveRepositories.Module +
        MyIdolsRepositories.Module
