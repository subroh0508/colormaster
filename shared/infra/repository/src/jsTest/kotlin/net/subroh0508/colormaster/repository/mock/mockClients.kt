package net.subroh0508.colormaster.repository.mock

import kotlinext.js.jsObject
import net.subroh0508.colormaster.api.authentication.AuthenticationClient
import net.subroh0508.colormaster.api.firestore.FirestoreClient

actual val mockFirestoreClient: FirestoreClient = jsObject()
actual val mockAuthenticationClient: AuthenticationClient = jsObject()
