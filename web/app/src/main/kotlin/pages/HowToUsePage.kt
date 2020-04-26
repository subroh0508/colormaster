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
import react.dom.*
import styled.css
import styled.styledSpan

@Suppress("FunctionName")
fun RBuilder.HowToUsePage() = StaticPage {
    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +"何ができるの？" }
            }
        }
        cardContent {
            p {
                +"THE IDOLM@STERシリーズに登場するアイドルを検索し、PC・スマホ端末上でイメージカラーのプレビューを表示することができます。"
            }
        }
    }

    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +"どんな機能があるの？" }
            }
        }

        cardContent {
            typography {
                attrs.variant = TypographyVariant.subtitle1
                styledSpan {
                    css { display = Display.flex; verticalAlign = VerticalAlign.middle }

                    icon { +"palette_icon" }
                    strong { +"プレビューモード" }
                }
            }

            p {
                +"「イメージカラー」「アイドル名」「カラーコード」を画面いっぱいに表示します。"
            }
            p {
                +"買い物中に目についた、"
                strong { +"担当カラーっぽいTシャツ・食器・文房具etc.の色を瞬時にスマホで確認することができます！" }
                +"最高ですね👍"
            }

            typography {
                attrs.variant = TypographyVariant.subtitle1
                styledSpan {
                    css { display = Display.flex; verticalAlign = VerticalAlign.middle }

                    icon { +"highlight_icon" }
                    strong { +"ペンライトモード" }
                }
            }

            p {
                +"「イメージカラー」のみを画面いっぱいに表示します。"
            }
            p {
                +"アニクラやDJバーで突如流れ出す担当曲！だけど今日はペンライトを持ってきていない😭"
            }
            p {
                +"こんな悲劇とはもうおさらば、"
                strong { +"あなたのスマホが瞬時にペンライトに生まれ変わります！" }
                +"優勝間違いなし🏆"
            }
        }
    }

    card {
        attrs.className = CARD_ELEMENT_CLASS_NAME
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH5 { +"使い方" }
            }
        }
        cardContent {
            ol {
                li {
                    +"検索画面でアイドルを検索する"
                }
                li {
                    +"プレビューしたいアイドルを選択する"
                }
                li {
                    strong { +"「プレビュー」" }
                    +"、または"
                    strong { +"「ペンライト」" }
                    +"ボタンをタップする"
                }
            }

            p {
                +"また、"
                strong { +"「プレビューしたいアイドルをダブルタップ」" }
                +"することで、"
                strong { +"ペンライトモードをショートカット起動" }
                +"させることができます。"
            }
        }
    }
}
