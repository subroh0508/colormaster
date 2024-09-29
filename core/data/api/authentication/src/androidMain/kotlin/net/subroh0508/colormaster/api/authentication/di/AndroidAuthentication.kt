package net.subroh0508.colormaster.api.authentication.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import net.subroh0508.colormaster.api.authentication.AuthenticationClient
import org.koin.dsl.module

actual object Authentication {
    actual val Module get() = module {
        single { AuthenticationClient(Firebase.auth) }
    }
}
