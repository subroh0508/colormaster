package components.atoms.button

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.ExperimentalComposeWebSvgApi
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.svg.G
import org.jetbrains.compose.web.svg.Path
import org.jetbrains.compose.web.svg.Svg
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.svg.SVGElement

@Composable
fun SignInWithGoogle(
    applyAttrs: (AttrsScope<HTMLButtonElement>.() -> Unit)? = null,
) = TextButton(
    applyAttrs,
    SignInWithGoogleStyle,
    leadingIcon = { GoogleIcon() },
) { Text("Sign in with Google") }

@OptIn(ExperimentalComposeWebSvgApi::class)
@Composable
private fun GoogleIcon() = Svg(
    "0 0 46 46",
    {
        attr("width", "46px")
        attr("height", "46px")
        attr("xmlns", "http://www.w3.org/2000/svg")
        attr("xmlns:xlink", "http://www.w3.org/1999/xlink")
        attr("xmlns:sketch", "http://www.bohemiancoding.com/sketch/ns")
        style {
            position(Position.Absolute)
            top((-4).px)
            left((-4).px)
        }
    },
) {
    G({
        id("Google-Button")
        attr("stroke", "none")
        attr("stroke-width", "1")
        attr("fill", "none")
        attr("fill-rule", "evenodd")
        attr("sketch:type", "MSPage")
    }) {
        G({
            id("btn_google_light_normal")
            attr("sketch:type", "MSArtboardGroup")
            attr("transform", "translate(-1.000000, -1.000000)")
        }) {
            G({
                id("logo_google_48dp")
                attr("sketch:type", "MSLayerGroup")
                attr("transform", "translate(15.000000, 15.000000)")
            }) { SvgPathGoogle() }
            G({
                id("handles_square")
                attr("sketch:type", "MSLayerGroup")
            })
        }
    }
}

@OptIn(ExperimentalComposeWebSvgApi::class)
@Composable
private fun ElementScope<SVGElement>.SvgPathGoogle() {
    Path(
        "M17.64,9.20454545 C17.64,8.56636364 17.5827273,7.95272727 17.4763636,7.36363636 L9,7.36363636 L9,10.845 L13.8436364,10.845 C13.635,11.97 13.0009091,12.9231818 12.0477273,13.5613636 L12.0477273,15.8195455 L14.9563636,15.8195455 C16.6581818,14.2527273 17.64,11.9454545 17.64,9.20454545 L17.64,9.20454545 Z",
        attrs = {
            id("Shape")
            attr("fill", "#4285F4")
            attr("sketch:type", "MSShapeGroup")
        },
    )
    Path(
        "M9,18 C11.43,18 13.4672727,17.1940909 14.9563636,15.8195455 L12.0477273,13.5613636 C11.2418182,14.1013636 10.2109091,14.4204545 9,14.4204545 C6.65590909,14.4204545 4.67181818,12.8372727 3.96409091,10.71 L0.957272727,10.71 L0.957272727,13.0418182 C2.43818182,15.9831818 5.48181818,18 9,18 L9,18 Z",
        attrs = {
            id("Shape")
            attr("fill", "#34A853")
            attr("sketch:type", "MSShapeGroup")
        },
    )
    Path(
        "M3.96409091,10.71 C3.78409091,10.17 3.68181818,9.59318182 3.68181818,9 C3.68181818,8.40681818 3.78409091,7.83 3.96409091,7.29 L3.96409091,4.95818182 L0.957272727,4.95818182 C0.347727273,6.17318182 0,7.54772727 0,9 C0,10.4522727 0.347727273,11.8268182 0.957272727,13.0418182 L3.96409091,10.71 L3.96409091,10.71 Z",
        attrs = {
            id("Shape")
            attr("fill", "#FBBC05")
            attr("sketch:type", "MSShapeGroup")
        },
    )
    Path(
        "M9,3.57954545 C10.3213636,3.57954545 11.5077273,4.03363636 12.4404545,4.92545455 L15.0218182,2.34409091 C13.4631818,0.891818182 11.4259091,0 9,0 C5.48181818,0 2.43818182,2.01681818 0.957272727,4.95818182 L3.96409091,7.29 C4.67181818,5.16272727 6.65590909,3.57954545 9,3.57954545 L9,3.57954545 Z",
        attrs = {
            id("Shape")
            attr("fill", "#EA4335")
            attr("sketch:type", "MSShapeGroup")
        },
    )
    Path(
        "M0,0 L18,0 L18,18 L0,18 L0,0 Z",
        attrs = {
            id("Shape")
            attr("sketch:type", "MSShapeGroup")
        },
    )
}

private object SignInWithGoogleStyle : TextButtonStyle() {
    override val button by style {
        height(40.px)
        padding(0.px)
        border(1.px, LineStyle.Solid, Color("#4285FA"))
        borderRadius(2.px)
        backgroundColor(Color("#4285FA"))

        className("mdc-button__label") style {
            color(Color.white)
            margin(0.px, 12.px, 0.px)
            fontWeight("bold")
            property("text-transform", "none")
            letterSpacing(0.px)
        }
        className("mdc-button__icon") style {
            height(38.px)
            width(38.px)
            marginRight(0.px)
            borderRadius(2.px, 0.px, 0.px, 2.px)
            backgroundColor(Color.white)
        }

        (className("mdc-button__ripple") + before) style {
            backgroundColor(Color.black)
        }
        (className("mdc-button__ripple") + after) style {
            backgroundColor(Color.black)
        }
    }
}
