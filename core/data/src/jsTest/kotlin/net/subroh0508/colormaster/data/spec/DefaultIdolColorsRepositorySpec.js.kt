package net.subroh0508.colormaster.data.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import net.subroh0508.colormaster.data.module.buildIdolColorsRepository
import net.subroh0508.colormaster.test.extension.flowToList
import net.subroh0508.colormaster.test.mockHttpClient

class JsDefaultIdolColorsRepositorySpec : FunSpec({
    test("#inChargeOfIdolIds: when sign in it should return id list") {
        val (repository, auth, _) = buildIdolColorsRepository {
            mockHttpClient()
        }

        val (instances, _) = flowToList(repository.inChargeOfIdolIds())
        auth.signInWithGoogle()
        repository.registerInChargeOf("Amami_Haruka")
        repository.registerInChargeOf("Kisaragi_Chihaya")
        repository.registerInChargeOf("Hoshii_Miki")

        repository.unregisterInChargeOf("Hoshii_Miki")
        repository.unregisterInChargeOf("Kisaragi_Chihaya")
        repository.unregisterInChargeOf("Amami_Haruka")

        instances.let {
            it should haveSize(7)
            it should containExactly(
                listOf(),
                listOf("Amami_Haruka"),
                listOf("Amami_Haruka", "Kisaragi_Chihaya"),
                listOf("Amami_Haruka", "Kisaragi_Chihaya", "Hoshii_Miki"),
                listOf("Amami_Haruka", "Kisaragi_Chihaya"),
                listOf("Amami_Haruka"),
                listOf(),
            )
        }
    }

    test("#favoriteIdolIds: when sign in it should return id list") {
        val (repository, auth, _) = buildIdolColorsRepository {
            mockHttpClient()
        }

        val (instances, _) = flowToList(repository.favoriteIdolIds())
        auth.signInWithGoogle()
        repository.favorite("Amami_Haruka")
        repository.favorite("Kisaragi_Chihaya")
        repository.favorite("Hoshii_Miki")

        repository.unfavorite("Hoshii_Miki")
        repository.unfavorite("Kisaragi_Chihaya")
        repository.unfavorite("Amami_Haruka")

        instances.let {
            it should haveSize(7)
            it should containExactly(
                listOf(),
                listOf("Amami_Haruka"),
                listOf("Amami_Haruka", "Kisaragi_Chihaya"),
                listOf("Amami_Haruka", "Kisaragi_Chihaya", "Hoshii_Miki"),
                listOf("Amami_Haruka", "Kisaragi_Chihaya"),
                listOf("Amami_Haruka"),
                listOf(),
            )
        }
    }
})
