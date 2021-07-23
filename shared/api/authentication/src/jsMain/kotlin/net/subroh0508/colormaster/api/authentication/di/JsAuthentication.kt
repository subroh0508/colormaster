package net.subroh0508.colormaster.api.authentication.di

import net.subroh0508.colormaster.api.authentication.AuthenticationClient
import net.subroh0508.colormaster.api.jsfirebaseauth.auth
import net.subroh0508.colormaster.api.jsfirebaseauth.firebase
import org.koin.dsl.module

actual object Authentication {
    actual val Module get() = module {
        auth
        single { AuthenticationClient(firebase.auth()) }
    }
}
