package net.subroh0508.colormaster.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.network.firestore.FirestoreClient
import net.subroh0508.colormaster.network.imasparql.ImasparqlClient
import net.subroh0508.colormaster.network.imasparql.json.IdolColorJson
import net.subroh0508.colormaster.network.imasparql.query.RandomQuery
import net.subroh0508.colormaster.network.imasparql.query.SearchByIdQuery
import net.subroh0508.colormaster.network.imasparql.query.SearchByLiveQuery
import net.subroh0508.colormaster.network.imasparql.query.SearchByNameQuery
import net.subroh0508.colormaster.network.imasparql.serializer.Response
import net.subroh0508.colormaster.model.*

internal class DefaultIdolColorsRepository(
    private val imasparqlClient: ImasparqlClient,
    private val firestoreClient: FirestoreClient,
    private val authClient: AuthClient,
) : IdolColorsRepository {
    private val idolsStateFlow = MutableStateFlow<List<IdolColor>>(listOf())
    private val inChargeOfIdolIdsStateFlow = MutableStateFlow<List<String>>(listOf())
    private val favoriteIdolIdsStateFlow = MutableStateFlow<List<String>>(listOf())

    override fun idols(limit: Int, lang: String): Flow<List<IdolColor>> {
        return idolsStateFlow.onStart {
            rand(limit, lang)
        }
    }

    override fun inChargeOfIdolIds(): Flow<List<String>> {
        return inChargeOfIdolIdsStateFlow.onStart {
            getInChargeOfIdolIds()
        }
    }

    override fun favoriteIdolIds(): Flow<List<String>> {
        return favoriteIdolIdsStateFlow.onStart {
            getFavoriteIdolIds()
        }
    }

    override suspend fun rand(
        limit: Int,
        lang: String,
    ) = imasparqlClient.search(
        RandomQuery(lang, limit).build(),
        IdolColorJson.serializer(),
    ).toIdolColors().also {
        idolsStateFlow.value = it
    }


    override suspend fun search(
        name: IdolName?,
        brands: Brands?,
        types: Set<Types>,
        lang: String,
    ) = imasparqlClient.search(
        SearchByNameQuery(lang, name?.value, brands?.queryStr, types.map(Types::queryStr)).build(),
        IdolColorJson.serializer(),
    ).toIdolColors().also {
        idolsStateFlow.value = it
    }

    override suspend fun search(
        liveName: LiveName,
        lang: String,
    ) = imasparqlClient.search(
        SearchByLiveQuery(lang, liveName.value).build(),
        IdolColorJson.serializer(),
    ).toIdolColors().also {
        idolsStateFlow.value = it
    }

    override suspend fun search(
        ids: List<String>,
        lang: String,
    ): List<IdolColor> {
        val idols =
            if (ids.isEmpty())
                listOf()
            else
                imasparqlClient.search(
                    SearchByIdQuery(lang, ids).build(),
                    IdolColorJson.serializer(),
                ).toIdolColors()

        return idols.also {
            idolsStateFlow.value = it
        }
    }

    override suspend fun getInChargeOfIdolIds() = getUserDocument().inCharges.also {
        inChargeOfIdolIdsStateFlow.value = it
    }

    override suspend fun getFavoriteIdolIds() = getUserDocument().favorites.also {
        favoriteIdolIdsStateFlow.value = it
    }

    override suspend fun registerInChargeOf(id: String) {
        val uid = currentUser?.uid ?: return

        firestoreClient.setUserDocument(
            uid,
            getUserDocument().copy(inCharges = getInChargeOfIdolIds() + id),
        )

        getInChargeOfIdolIds()
    }

    override suspend fun unregisterInChargeOf(id: String) {
        val uid = currentUser?.uid ?: return

        firestoreClient.setUserDocument(
            uid,
            getUserDocument().copy(inCharges = getInChargeOfIdolIds() - id),
        )

        getInChargeOfIdolIds()
    }

    override suspend fun favorite(id: String) {
        val uid = currentUser?.uid ?: return

        firestoreClient.setUserDocument(
            uid,
            getUserDocument().copy(favorites = getFavoriteIdolIds() + id),
        )

        getFavoriteIdolIds()
    }

    override suspend fun unfavorite(id: String) {
        val uid = currentUser?.uid ?: return

        firestoreClient.setUserDocument(
            uid,
            getUserDocument().copy(favorites = getFavoriteIdolIds() - id),
        )

        getFavoriteIdolIds()
    }

    private val currentUser get() = authClient.currentUser

    private suspend fun getUserDocument() = firestoreClient.getUserDocument(currentUser?.uid)

    private fun Response<IdolColorJson>.toIdolColors() = results
        .bindings
        .mapNotNull { (idMap, nameMap, colorMap) ->
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
