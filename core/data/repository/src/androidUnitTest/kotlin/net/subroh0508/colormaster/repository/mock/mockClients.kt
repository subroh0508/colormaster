package net.subroh0508.colormaster.repository.mock

import io.mockk.mockk
import net.subroh0508.colormaster.network.authentication.AuthenticationClient
import net.subroh0508.colormaster.network.firestore.FirestoreClient

actual val mockFirestoreClient: FirestoreClient = mockk()
actual val mockAuthenticationClient: AuthenticationClient = mockk()
