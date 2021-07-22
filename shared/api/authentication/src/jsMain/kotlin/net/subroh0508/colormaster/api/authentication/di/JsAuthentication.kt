package net.subroh0508.colormaster.api.authentication.di

import net.subroh0508.colormaster.api.authentication.AuthenticationClient
import net.subroh0508.colormaster.api.jsfirebaseapp.firebase
import org.koin.dsl.module

actual object Authentication {
    actual val Module get() = module {
        single { AuthenticationClient(firebase.auth()) }
    }
}
