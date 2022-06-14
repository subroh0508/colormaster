package utilities

import kotlinx.browser.document
import org.jetbrains.compose.web.dom.ElementBuilder
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

class TagElementBuilder(private val tagName: String) : ElementBuilder<HTMLElement> {
    private val el: Element by lazy { document.createElement(tagName) }
    @Suppress("UNCHECKED_CAST")
    override fun create(): HTMLElement = el.cloneNode() as HTMLElement
}
