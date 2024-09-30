package net.subroh0508.colormaster.test.data

import net.subroh0508.colormaster.network.auth.model.FirebaseUser
import net.subroh0508.colormaster.network.auth.model.Provider

val anonymous = FirebaseUser(
    "xxx-xxx-xxx",
    listOf(Provider(Provider.PROVIDER_ANONYMOUS, null, null)),
)

val fromGoogle = FirebaseUser(
    "yyy-yyy-yyy",
    listOf(Provider(Provider.PROVIDER_GOOGLE, "example@gmail.com", "Google User")),
)
