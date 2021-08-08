package net.subroh0508.colormaster.api.firestore.di

import com.google.firebase.firestore.FirebaseFirestore
import net.subroh0508.colormaster.api.firestore.FirestoreClient
import org.koin.dsl.module

actual object Firestore {
    actual val Module get() = module {
        single { FirestoreClient(FirebaseFirestore.getInstance()) }
    }
}
