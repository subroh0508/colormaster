@file:JsModule("react")

package utilities

import react.FunctionComponent
import react.RProps

external fun <P : RProps> memo(
    fc: FunctionComponent<P>,
    compare: (previous: P, next: P) -> Boolean
): FunctionComponent<P>
