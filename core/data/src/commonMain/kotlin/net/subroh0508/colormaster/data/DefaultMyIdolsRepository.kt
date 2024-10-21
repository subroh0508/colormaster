package net.subroh0508.colormaster.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.MyIdolsRepository
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.network.firestore.FirestoreClient
import net.subroh0508.colormaster.network.imasparql.ImasparqlClient
import net.subroh0508.colormaster.network.imasparql.json.IdolColorJson
import net.subroh0508.colormaster.network.imasparql.query.SearchByIdQuery

internal class DefaultMyIdolsRepository(
    private val imasparqlClient: ImasparqlClient,
    private val firestoreClient: FirestoreClient,
    private val authClient: AuthClient,
) : MyIdolsRepository {
    private val inChargeOfIdolsStateFlow = MutableStateFlow<List<IdolColor>>(listOf())
    private val favoriteIdolsStateFlow = MutableStateFlow<List<IdolColor>>(listOf())

    override fun getInChargeOfIdolsStream(lang: String): Flow<List<IdolColor>> {
        return inChargeOfIdolsStateFlow.onStart {
            inChargeOfIdolsStateFlow.value = search(getUserDocument().inCharges, lang)
        }
    }

    override fun getFavoriteIdolsStream(lang: String): Flow<List<IdolColor>> {
        return favoriteIdolsStateFlow.onStart {
            favoriteIdolsStateFlow.value = search(getUserDocument().favorites, lang)
        }
    }

    private suspend fun search(
        ids: List<String>,
        lang: String,
    ) = if (ids.isEmpty())
            listOf()
        else
            imasparqlClient.search(
                SearchByIdQuery(lang, ids).build(),
                IdolColorJson.serializer(),
            ).toIdolColors()

    private val currentUser get() = authClient.currentUser

    private suspend fun getUserDocument() = firestoreClient.getUserDocument(currentUser?.uid)
}
