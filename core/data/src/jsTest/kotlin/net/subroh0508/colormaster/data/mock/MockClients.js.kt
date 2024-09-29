package net.subroh0508.colormaster.data.mock

import kotlinx.js.jso
import net.subroh0508.colormaster.network.authentication.AuthenticationClient
import net.subroh0508.colormaster.network.firestore.FirestoreClient

actual val MockFirestoreClient: FirestoreClient = jso()
actual val MockAuthenticationClient: AuthenticationClient = jso()
