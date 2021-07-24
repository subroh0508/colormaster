package net.subroh0508.colormaster.repository.internal

import net.subroh0508.colormaster.api.authentication.AuthenticationClient
import net.subroh0508.colormaster.api.firestore.COLLECTION_USERS
import net.subroh0508.colormaster.api.firestore.DocumentSnapshot
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
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import net.subroh0508.colormaster.repository.IdolColorsRepository

internal class IdolColorsRepositoryImpl(
    private val imasparqlClient: ImasparqlClient,
    private val firestoreClient: FirestoreClient,
    private val authenticationClient: AuthenticationClient,
    private val appPreference: AppPreference,
) : IdolColorsRepository {
    override suspend fun rand(limit: Int) =
        imasparqlClient.search(
            RandomQuery(appPreference.lang.code, limit).build(),
            IdolColorJson.serializer(),
        ).toIdolColors()

    override suspend fun search(name: IdolName?, brands: Brands?, types: Set<Types>) =
        imasparqlClient.search(
            SearchByNameQuery(appPreference.lang.code, name?.value, brands?.queryStr, types.map(Types::queryStr)).build(),
            IdolColorJson.serializer(),
        ).toIdolColors()

    override suspend fun search(liveName: LiveName) =
        imasparqlClient.search(
            SearchByLiveQuery(appPreference.lang.code, liveName.value).build(),
            IdolColorJson.serializer(),
        ).toIdolColors()

    override suspend fun search(ids: List<String>)  =
        imasparqlClient.search(
            SearchByIdQuery(appPreference.lang.code, ids).build(),
            IdolColorJson.serializer(),
        ).toIdolColors()

    override suspend fun getFavoriteIdolIds() = getUserDocument().favorites

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

    private fun getUsersCollection() = firestoreClient.collection(COLLECTION_USERS)
    private suspend fun getUserDocument(): UserDocument {
        val uid = currentUser?.uid ?: return UserDocument()

        return getUsersCollection().document(uid)
            .get()
            .takeIf(DocumentSnapshot::exists)
            ?.data(UserDocument.serializer())
            ?: UserDocument()
    }

    private fun Response<IdolColorJson>.toIdolColors(): List<IdolColor> = results.bindings.mapNotNull { (idMap, nameMap, colorMap) ->
        val id = idMap["value"] ?: return@mapNotNull null
        val name = nameMap["value"] ?: return@mapNotNull null
        val color = colorMap["value"] ?: return@mapNotNull null

        IdolColor(id, name, HexColor(color))
    }
}
