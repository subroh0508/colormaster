package net.subroh0508.colormaster.model

interface Types {
    val queryStr: String

    enum class CinderellaGirls(override val queryStr: String) : Types {
        CU("Cu"), CO("Co"), PA("Pa")
    }

    enum class MillionLive(override val queryStr: String) : Types {
        PRINCESS("Princess"), FAIRY("Fairy"), ANGEL("Angel")
    }

    enum class SideM(override val queryStr: String) : Types {
        PHYSICAL("フィジカル"), INTELLIGENT("インテリ"), MENTAL("メンタル")
    }
}
