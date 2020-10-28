package net.subroh0508.colormaster.db.di

import net.subroh0508.colormaster.db.IdolColorsDatabase
import net.subroh0508.colormaster.db.internal.IdolColorsDatabaseClient
import org.koin.dsl.module

object JsIdolColorsDatabases {
    val Module get() = module {
        single<IdolColorsDatabase> { IdolColorsDatabaseClient() }
    }
}
