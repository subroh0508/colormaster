package net.subroh0508.colormaster.repository.mock

import net.subroh0508.colormaster.network.authentication.AuthenticationClient
import net.subroh0508.colormaster.network.firestore.FirestoreClient

expect val mockFirestoreClient: FirestoreClient
expect val mockAuthenticationClient: AuthenticationClient
