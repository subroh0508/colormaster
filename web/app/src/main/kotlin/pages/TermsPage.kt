package pages

import components.atoms.link
import materialui.components.card.card
import materialui.components.cardcontent.cardContent
import materialui.components.cardheader.cardHeader
import materialui.components.typography.typographyH5
import react.RBuilder
import react.PropsWithChildren
import react.child
import react.dom.p
import react.functionComponent
import utilities.Trans
import utilities.invoke
import utilities.useTranslation

@Suppress("FunctionName")
fun RBuilder.TermsPage() = StaticPage { child(TermComponent) }

private val TermComponent = functionComponent<PropsWithChildren> {
    val (t, _) = useTranslation()

    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +t("about.terms.top.title") }
            }
        }
        cardContent {
            Trans {
                attrs.i18nKey = "about.terms.top.description"

                p { link(href = "https://twitter.com/subroh_0508") }
                p { +"dummy" }
            }
        }
    }

    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +t("about.terms.disclaimer.title") }
            }
        }
        cardContent {
            Trans {
                attrs.i18nKey = "about.terms.disclaimer.description"

                p { +"dummy" }
                p { +"dummy" }
            }
        }
    }

    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +t("about.terms.cookie.title") }
            }
        }
        cardContent {
            Trans {
                attrs.i18nKey = "about.terms.cookie.description"

                p { +"dummy" }
                p { +"dummy" }
                p { +"dummy" }
            }
        }
    }
}
