@file:JsModule("react")

package utilities

import react.FunctionalComponent
import react.RProps

external fun <P : RProps> memo(
    fc: FunctionalComponent<P>,
    compare: (previous: P, next: P) -> Boolean
): FunctionalComponent<P>
