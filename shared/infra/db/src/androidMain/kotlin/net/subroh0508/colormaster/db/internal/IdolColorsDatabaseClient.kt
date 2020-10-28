package net.subroh0508.colormaster.db.internal

import android.content.SharedPreferences
import androidx.core.content.edit
import net.subroh0508.colormaster.db.IdolColorsDatabase
import net.subroh0508.colormaster.db.IdolColorsDatabase.Companion.FAVORITE_KEY

internal actual class IdolColorsDatabaseClient(
    private val preferences: SharedPreferences
) : IdolColorsDatabase {
    override suspend fun addFavorite(id: String) =
        preferences.edit { putStringSet(FAVORITE_KEY, idsFromLocal.apply { add(id) }) }

    override suspend fun removeFavorite(id: String) =
        preferences.edit { putStringSet(FAVORITE_KEY, idsFromLocal.apply { remove(id) }) }

    private val idsFromLocal: MutableSet<String>
        get() = preferences.getStringSet(FAVORITE_KEY, setOf())?.toMutableSet() ?: mutableSetOf()
}
