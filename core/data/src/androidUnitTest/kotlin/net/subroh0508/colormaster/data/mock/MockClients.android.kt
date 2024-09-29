package net.subroh0508.colormaster.data.mock

import io.mockk.mockk
import net.subroh0508.colormaster.network.authentication.AuthenticationClient
import net.subroh0508.colormaster.network.firestore.FirestoreClient

actual val MockFirestoreClient: FirestoreClient = mockk()
actual val MockAuthenticationClient: AuthenticationClient = mockk()
