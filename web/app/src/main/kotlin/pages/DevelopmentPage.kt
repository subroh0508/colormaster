package pages

import materialui.components.card.card
import materialui.components.cardcontent.cardContent
import materialui.components.cardheader.cardHeader
import materialui.components.typography.typographyH5
import react.RBuilder
import react.dom.*

@Suppress("FunctionName")
fun RBuilder.DevelopmentPage() = StaticPage {
    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +"イメージカラーはどこから取ってきてるの？" }
            }
        }
        cardContent {
            p {
                +"必要なデータは全て"
                a {
                    attrs.href = "https://sparql.crssnky.xyz/imas/"
                    +"im@sparql"
                }
                +"から取得しています。"
            }
            p {
                +"データに間違いを見つけた場合は、"
                a {
                    attrs.href = "https://github.com/imas/imasparql/issues"
                    +"im@sparqlのIssueページ"
                }
                +"にご報告ください。"
            }
        }
    }
    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +"フロントエンドの実装はどうなっているの？" }
            }
        }
        cardContent {
            p {
                +"このWebアプリは、"
                a {
                    attrs.href = "https://kotlinlang.org/docs/reference/multiplatform.html"
                    strong { +"Kotlin Multiplatform" }
                }
                +"を使って実装しています。ソースコードに占めるKotlinの割合は"
                strong { +"95%を超えています。" }
            }
            p {
                +"つまり、このWebアプリは765プロダクション事務員である"
                strong { +"音無小鳥さんとほぼ同じ存在です。" }
                +"ここテストに出ます。覚えておきましょう。"
            }
            p {
                +"また、UIフレームワークとして"
                a {
                    attrs.href = "https://material-ui.com/"
                    +"Material-UI"
                }
                +"も利用しています。"
            }
        }
    }
    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +"バグを見つけた！機能追加して欲しい！" }
            }
        }
        cardContent {
            p {
                a {
                    attrs.href = "https://github.com/subroh0508/colormaster/issues"
                    +"COLOR M@STERのIssueページ"
                }
                +"に報告ください。"
            }
            p {
                +"St@r・Pull Requestも歓迎です。ついでに投げ銭とかしてくれると泣いて喜びます。"
            }

            ul {
                li {
                    +"ほしいものリスト: "
                    a {
                        attrs.href = "https://www.amazon.jp/hz/wishlist/ls/34TBOXPWOUD8W?ref_=wl_share"
                        +"Link"
                    }
                }
            }
        }
    }
}
