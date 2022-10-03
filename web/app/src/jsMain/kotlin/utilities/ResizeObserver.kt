package utilities

import org.w3c.dom.Element

external class BoxSize {
    val blockSize: Number
    val inlineSize: Number
}

external class ResizeObserverEntry {
    val target: Element
    val borderBoxSize: Array<BoxSize>
    val contentBoxSize: Array<BoxSize>
    val devicePixelContentBoxSize: Array<BoxSize>
}

external class ResizeObserver(handler: (Array<ResizeObserverEntry>, ResizeObserver) -> Unit) {
    fun observe(target: Element, options: dynamic = definedExternally)
    fun unobserve(target: Element)
    fun disconnect()
}
