import components.templates.appFrame
import components.templates.colorGrids
import kotlinx.coroutines.MainScope
import materialui.components.cssbaseline.cssBaseline
import net.subroh0508.colormaster.components.core.AppModule
import org.kodein.di.Kodein
import react.child
import react.dom.div
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window

val mainScope = MainScope()
val appKodein = Kodein { import(AppModule) }

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            cssBaseline {  }
            child(appFrame()) {
                child(colorGrids()) {  }
            }
        }
    }
}
