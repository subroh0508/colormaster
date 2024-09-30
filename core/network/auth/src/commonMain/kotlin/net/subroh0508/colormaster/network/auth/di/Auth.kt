package net.subroh0508.colormaster.network.auth.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import net.subroh0508.colormaster.network.auth.AuthClient
import org.koin.core.module.Module
import org.koin.dsl.module

object Auth {
    val Module: Module get() = module {
        single { AuthClient(Firebase.auth) }
    }
}
