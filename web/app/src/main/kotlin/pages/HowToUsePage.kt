package pages

import materialui.components.paper.paper
import materialui.components.typography.typographyH4
import materialui.components.typography.typographyH5
import react.RBuilder
import react.RProps
import react.child
import react.dom.p
import react.functionalComponent

@Suppress("FunctionName")
fun RBuilder.HowToUsePage() = StaticPage {
    paper {
        typographyH4 {
            +"何ができるの？"
        }

        p {
            +"THE IDOLM@STERシリーズに登場するアイドルのイメージカラーを検索、PC・スマホ端末上でプレビューを表示することができます"
        }
    }

    paper {

    }
}
