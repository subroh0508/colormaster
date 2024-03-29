package net.subroh0508.colormaster.api.authentication.di

import com.google.firebase.auth.FirebaseAuth
import net.subroh0508.colormaster.api.authentication.AuthenticationClient
import org.koin.dsl.module

actual object Authentication {
    actual val Module get() = module {
        single { AuthenticationClient(FirebaseAuth.getInstance()) }
    }
}
