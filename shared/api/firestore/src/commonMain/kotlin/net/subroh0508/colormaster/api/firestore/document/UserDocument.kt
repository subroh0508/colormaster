package net.subroh0508.colormaster.api.firestore.document

import kotlinx.serialization.Serializable

@Serializable
data class UserDocument(
    val favorites: List<String> = listOf(),
)
