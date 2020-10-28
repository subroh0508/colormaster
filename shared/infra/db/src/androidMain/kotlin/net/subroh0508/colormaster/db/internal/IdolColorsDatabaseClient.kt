package net.subroh0508.colormaster.db.internal

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import net.subroh0508.colormaster.db.IdolColorsDatabase
import net.subroh0508.colormaster.db.IdolColorsDatabase.Companion.FAVORITE_DB
import net.subroh0508.colormaster.db.IdolColorsDatabase.Companion.FAVORITE_KEY

internal actual class IdolColorsDatabaseClient(
    app: Application,
) : IdolColorsDatabase {
    private val preferences = app.getSharedPreferences(FAVORITE_DB, Context.MODE_PRIVATE)

    override suspend fun getFavorites() =
        preferences.getStringSet(FAVORITE_KEY, setOf()) ?: setOf()

    override suspend fun addFavorite(id: String) =
        preferences.edit { putStringSet(FAVORITE_KEY, getFavorites().toMutableSet().apply { add(id) }) }

    override suspend fun removeFavorite(id: String) =
        preferences.edit { putStringSet(FAVORITE_KEY, getFavorites().toMutableSet().apply { remove(id) }) }
}
