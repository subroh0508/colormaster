package net.subroh0508.colormaster.repository.mock

import kotlinx.js.jso
import net.subroh0508.colormaster.api.authentication.AuthenticationClient
import net.subroh0508.colormaster.api.firestore.FirestoreClient

actual val mockFirestoreClient: FirestoreClient = jso()
actual val mockAuthenticationClient: AuthenticationClient = jso()
