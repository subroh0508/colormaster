package net.subroh0508.colormaster.model

interface Types {
    val queryStr: String

    enum class CINDERELLA_GIRLS(override val queryStr: String) : Types {
        CU("Cu"), CO("Co"), PA("Pa")
    }

    enum class MILLION_LIVE(override val queryStr: String) : Types {
        PRINCESS("Princess"), FAIRY("Fairy"), ANGEL("Angel")
    }

    enum class SIDE_M(override val queryStr: String) : Types {
        PHYSICAL("フィジカル"), INTELLIGENT("インテリ"), MENTAL("メンタル")
    }
}
