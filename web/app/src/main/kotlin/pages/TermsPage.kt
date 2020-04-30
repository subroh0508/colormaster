package pages

import components.atoms.link
import materialui.components.card.card
import materialui.components.cardcontent.cardContent
import materialui.components.cardheader.cardHeader
import materialui.components.typography.typographyH5
import react.RBuilder
import react.dom.p

@Suppress("FunctionName")
fun RBuilder.TermsPage() = StaticPage {
    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +"利用規約" }
            }
        }
        cardContent {
            p {
                +"この利用規約は、"
                link(
                    href = "https://twitter.com/subroh_0508",
                    label = "にしこりさぶろ〜"
                )
                +"(以下「開発者」という)が、COLOR M@STER(以下「当アプリ」という)で提供するサービスの利用条件を定めるものです。"
            }
            p {
                +"利用者の皆様(以下「利用者」という)が当アプリを利用する場合、この規約(以下「本規約」という)に定める事項を承諾したものとみなします。"
            }
        }
    }

    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +"1. 当アプリの利用は自己責任でお願いします" }
            }
        }
        cardContent {
            p {
                +"開発者は当アプリにおける情報の正確性、特定の目的への適合性，セキュリティなどに関する欠陥，エラーやバグ等がないことを保証しておりません。"
            }
            p {
                +"また、当アプリを利用したことで生じたあらゆる損害・不利益等に対して、開発者はいかなる責任も負いません。"
            }
        }
    }
}
