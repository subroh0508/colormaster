package utilities

import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event

fun Event.inputTarget() = target as HTMLInputElement
