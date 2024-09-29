package net.subroh0508.colormaster.repository.mock

import kotlinx.js.jso
import net.subroh0508.colormaster.network.authentication.AuthenticationClient
import net.subroh0508.colormaster.network.firestore.FirestoreClient

actual val mockFirestoreClient: FirestoreClient = jso()
actual val mockAuthenticationClient: AuthenticationClient = jso()
