package net.subroh0508.colormaster.network.authentication.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import net.subroh0508.colormaster.network.authentication.AuthenticationClient
import org.koin.core.module.Module
import org.koin.dsl.module

object Authentication {
    val Module: Module get() = module {
        single { AuthenticationClient(Firebase.auth) }
    }
}
