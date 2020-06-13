package utilities

import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event

fun Event.inputTarget() = target as HTMLInputElement
fun Event.buttonTarget() = target as HTMLButtonElement
