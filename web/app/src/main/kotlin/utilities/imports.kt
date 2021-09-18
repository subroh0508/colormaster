@file:JsModule("react")

package utilities

import react.FunctionComponent
import react.PropsWithChildren

external fun <P : PropsWithChildren> memo(
    fc: FunctionComponent<P>,
    compare: (previous: P, next: P) -> Boolean
): FunctionComponent<P>
