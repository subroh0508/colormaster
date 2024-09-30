package net.subroh0508.colormaster.data.mock

import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.network.firestore.FirestoreClient

expect val MockFirestoreClient: FirestoreClient
expect val MockAuthClient: AuthClient
