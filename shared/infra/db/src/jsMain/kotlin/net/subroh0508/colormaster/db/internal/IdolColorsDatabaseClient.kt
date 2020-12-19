package net.subroh0508.colormaster.db.internal

import net.subroh0508.colormaster.db.IdolColorsDatabase

internal actual class IdolColorsDatabaseClient : IdolColorsDatabase {
    override suspend fun getFavorites() = setOf<String>()
    override suspend fun addFavorite(id: String) = Unit
    override suspend fun removeFavorite(id: String) = Unit
}
