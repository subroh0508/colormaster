package net.subroh0508.colormaster.repository.internal

import net.subroh0508.colormaster.api.authentication.AuthenticationClient
import net.subroh0508.colormaster.api.firestore.FirestoreClient
import net.subroh0508.colormaster.api.firestore.document.UserDocument
import net.subroh0508.colormaster.api.imasparql.ImasparqlClient
import net.subroh0508.colormaster.api.imasparql.json.IdolColorJson
import net.subroh0508.colormaster.api.imasparql.query.RandomQuery
import net.subroh0508.colormaster.api.imasparql.query.SearchByIdQuery
import net.subroh0508.colormaster.api.imasparql.query.SearchByLiveQuery
import net.subroh0508.colormaster.api.imasparql.query.SearchByNameQuery
import net.subroh0508.colormaster.api.imasparql.serializer.Response
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.repository.IdolColorsRepository

internal class IdolColorsRepositoryImpl(
    private val imasparqlClient: ImasparqlClient,
    private val firestoreClient: FirestoreClient,
    private val authenticationClient: AuthenticationClient,
) : IdolColorsRepository {
    override suspend fun rand(limit: Int, lang: String) =
        imasparqlClient.search(
            RandomQuery(lang, limit).build(),
            IdolColorJson.serializer(),
        ).toIdolColors()

    override suspend fun search(name: IdolName?, brands: Brands?, types: Set<Types>, lang: String) =
        imasparqlClient.search(
            SearchByNameQuery(lang, name?.value, brands?.queryStr, types.map(Types::queryStr)).build(),
            IdolColorJson.serializer(),
        ).toIdolColors()

    override suspend fun search(liveName: LiveName, lang: String) =
        imasparqlClient.search(
            SearchByLiveQuery(lang, liveName.value).build(),
            IdolColorJson.serializer(),
        ).toIdolColors()

    override suspend fun search(ids: List<String>, lang: String) =
        if (ids.isEmpty())
            listOf()
        else
            imasparqlClient.search(
                SearchByIdQuery(lang, ids).build(),
                IdolColorJson.serializer(),
            ).toIdolColors()

    override suspend fun getInChargeOfIdolIds() = getUserDocument().inCharges

    override suspend fun getFavoriteIdolIds() = getUserDocument().favorites

    override suspend fun registerInChargeOf(id: String) {
        val uid = currentUser?.uid ?: return

        getUsersCollection().document(uid).set(
            UserDocument.serializer(),
            getUserDocument().copy(inCharges = getInChargeOfIdolIds() + id),
            merge = true,
        )
    }

    override suspend fun unregisterInChargeOf(id: String) {
        val uid = currentUser?.uid ?: return

        getUsersCollection().document(uid).set(
            UserDocument.serializer(),
            getUserDocument().copy(inCharges = getInChargeOfIdolIds() - id),
            merge = true,
        )
    }

    override suspend fun favorite(id: String) {
        val uid = currentUser?.uid ?: return

        getUsersCollection().document(uid).set(
            UserDocument.serializer(),
            getUserDocument().copy(favorites = getFavoriteIdolIds() + id),
            merge = true,
        )
    }

    override suspend fun unfavorite(id: String) {
        val uid = currentUser?.uid ?: return

        getUsersCollection().document(uid).set(
            UserDocument.serializer(),
            getUserDocument().copy(favorites = getFavoriteIdolIds() - id),
            merge = true,
        )
    }

    private val currentUser get() = authenticationClient.currentUser

    private fun getUsersCollection() = firestoreClient.getUsersCollection()
    private suspend fun getUserDocument() = firestoreClient.getUserDocument(currentUser?.uid)

    private fun Response<IdolColorJson>.toIdolColors(): List<IdolColor> = results.bindings.mapNotNull { (idMap, nameMap, colorMap) ->
        val id = idMap["value"] ?: return@mapNotNull null
        val name = nameMap["value"] ?: return@mapNotNull null
        val color = colorMap["value"] ?: return@mapNotNull null

        val intColor = Triple(
            color.substring(0, 2).toInt(16),
            color.substring(2, 4).toInt(16),
            color.substring(4, 6).toInt(16),
        )

        IdolColor(id, name, intColor)
    }
}
