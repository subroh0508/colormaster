package net.subroh0508.colormaster.api.firestore.di

import net.subroh0508.colormaster.api.firestore.FirestoreClient
import net.subroh0508.colormaster.api.jsfirebaseapp.firebase
import net.subroh0508.colormaster.api.jsfirebaseapp.firestore
import org.koin.dsl.module

actual object Firestore {
    actual val Module get() = module {
        firestore
        single { FirestoreClient(firebase.firestore()) }
    }
}
