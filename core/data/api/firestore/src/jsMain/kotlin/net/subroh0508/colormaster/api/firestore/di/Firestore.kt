package net.subroh0508.colormaster.api.firestore.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import net.subroh0508.colormaster.api.firestore.FirestoreClient
import org.koin.dsl.module

actual object Firestore {
    actual val Module get() = module {
        single { FirestoreClient(Firebase.firestore) }
    }
}
