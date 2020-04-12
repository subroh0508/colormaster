@file:JsModule("react-router-dom")

package utilities

import react.RProps
import react.router.dom.RouteResultHistory
import react.router.dom.RouteResultLocation
import react.router.dom.RouteResultMatch

@JsName("useHistory")
external fun useHistory(): RouteResultHistory

@JsName("useLocation")
external fun useLocation(): RouteResultLocation

@JsName("useParams")
external fun rawUseParams(): dynamic

@JsName("useRouteMatch")
external fun <T: RProps> rawRouteMatch(options: dynamic): RouteResultMatch<T>
