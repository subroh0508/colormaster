package pages

import components.atoms.link
import materialui.components.card.card
import materialui.components.cardcontent.cardContent
import materialui.components.cardheader.cardHeader
import materialui.components.typography.typographyH5
import react.RBuilder
import react.PropsWithChildren
import react.child
import react.dom.*
import react.functionComponent
import utilities.Trans
import utilities.invoke
import utilities.useTranslation

@Suppress("FunctionName")
fun RBuilder.DevelopmentPage() = StaticPage { child(DevelopmentComponent) }

private val DevelopmentComponent = functionComponent<PropsWithChildren> {
    val (t, _) = useTranslation()

    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +t("about.development.imasparql.title") }
            }
        }
        cardContent {
            Trans {
                attrs.i18nKey = "about.development.imasparql.description"

                p { link(href = "https://sparql.crssnky.xyz/imas/") }
                p { link(href = "https://github.com/imas/imasparql/issues") }
            }
        }
    }
    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +t("about.development.frontend.title") }
            }
        }
        cardContent {
            Trans {
                attrs.i18nKey = "about.development.frontend.description"

                p { link(href = "https://kotlinlang.org/docs/reference/multiplatform.html") }
                p { +"dummy"}
                p { link(href = "https://material-ui.com") }
            }
        }
    }
    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +t("about.development.requests.title") }
            }
        }
        cardContent {
            Trans {
                attrs.i18nKey = "about.development.requests.description"

                p { link(href = "https://github.com/subroh0508/colormaster/issues") }
                p { +"dummy" }

                ul {
                    li { link(href = "https://www.amazon.jp/hz/wishlist/ls/34TBOXPWOUD8W?ref_=wl_share") }
                }
            }
        }
    }
}
