package net.subroh0508.colormaster.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import net.subroh0508.colormaster.data.extension.search
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.MyIdolsRepository
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.network.firestore.FirestoreClient
import net.subroh0508.colormaster.network.imasparql.ImasparqlClient

internal class DefaultMyIdolsRepository(
    private val imasparqlClient: ImasparqlClient,
    private val firestoreClient: FirestoreClient,
    private val authClient: AuthClient,
) : MyIdolsRepository {
    private val inChargeOfIdolsStateFlow = MutableStateFlow<List<IdolColor>>(listOf())
    private val favoriteIdolsStateFlow = MutableStateFlow<List<IdolColor>>(listOf())

    override fun getInChargeOfIdolsStream(lang: String): Flow<List<IdolColor>> {
        return inChargeOfIdolsStateFlow.onStart {
            inChargeOfIdolsStateFlow.value = imasparqlClient.search(getUserDocument().inCharges, lang)
        }
    }

    override fun getFavoriteIdolsStream(lang: String): Flow<List<IdolColor>> {
        return favoriteIdolsStateFlow.onStart {
            favoriteIdolsStateFlow.value = imasparqlClient.search(getUserDocument().favorites, lang)
        }
    }

    private val currentUser get() = authClient.currentUser

    private suspend fun getUserDocument() = firestoreClient.getUserDocument(currentUser?.uid)
}
