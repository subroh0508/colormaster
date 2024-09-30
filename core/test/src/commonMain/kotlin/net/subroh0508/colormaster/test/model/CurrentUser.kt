package net.subroh0508.colormaster.test.model

import net.subroh0508.colormaster.model.auth.CredentialProvider
import net.subroh0508.colormaster.model.auth.CurrentUser
import net.subroh0508.colormaster.test.data.fromGoogle

val GoogleUser = CurrentUser(
    fromGoogle.uid,
    listOf(CredentialProvider.Google(fromGoogle.providers.first().email ?: "")),
)
