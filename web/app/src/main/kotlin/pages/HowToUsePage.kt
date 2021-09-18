package pages

import kotlinx.css.Display
import kotlinx.css.VerticalAlign
import kotlinx.css.display
import kotlinx.css.verticalAlign
import materialui.components.card.card
import materialui.components.cardcontent.cardContent
import materialui.components.cardheader.cardHeader
import materialui.components.icon.icon
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.components.typography.typographyH5
import react.RBuilder
import react.PropsWithChildren
import react.child
import react.dom.*
import react.functionComponent
import styled.css
import styled.styledSpan
import utilities.Trans
import utilities.invoke
import utilities.useTranslation

@Suppress("FunctionName")
fun RBuilder.HowToUsePage() = StaticPage { child(HowToUseComponent) }

val HowToUseComponent = functionComponent<PropsWithChildren> {
    val (t, _) = useTranslation()

    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +t("about.howToUse.introduction.title") }
            }
        }
        cardContent {
            Trans { attrs.i18nKey = "about.howToUse.introduction.description" }
        }
    }

    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +t("about.howToUse.features.title") }
            }
        }

        cardContent {
            typography {
                attrs.variant = TypographyVariant.subtitle1
                styledSpan {
                    css { display = Display.flex; verticalAlign = VerticalAlign.middle }

                    icon { +"palette_icon" }
                    strong { +t("about.howToUse.features.preview.title") }
                }
            }

            Trans { attrs.i18nKey = "about.howToUse.features.preview.description" }

            typography {
                attrs.variant = TypographyVariant.subtitle1
                styledSpan {
                    css { display = Display.flex; verticalAlign = VerticalAlign.middle }

                    icon { +"highlight_icon" }
                    strong { +t("about.howToUse.features.penlight.title") }
                }
            }

            Trans { attrs.i18nKey = "about.howToUse.features.penlight.description" }
        }
    }

    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +t("about.howToUse.howToUse.title") }
            }
        }
        cardContent {
            Trans {
                attrs.i18nKey = "about.howToUse.howToUse.description"

                ol {
                    li { +"dummy" }
                    li { +"dummy" }
                    li { +"dummy" }
                }
            }

            Trans {
                attrs.i18nKey = "about.howToUse.howToUse.descriptionMore"

                strong { +"dummy" }
                icon { +"dummy" }
                icon { +"dummy" }
            }
        }
    }
}
