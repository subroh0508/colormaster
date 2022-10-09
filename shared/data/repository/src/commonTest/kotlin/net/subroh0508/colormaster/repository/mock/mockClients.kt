package net.subroh0508.colormaster.repository.mock

import net.subroh0508.colormaster.api.authentication.AuthenticationClient
import net.subroh0508.colormaster.api.firestore.FirestoreClient

expect val mockFirestoreClient: FirestoreClient
expect val mockAuthenticationClient: AuthenticationClient
