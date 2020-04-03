package utilities

import react.useEffect

fun useEffectDidMount(effect: () -> Unit) = useEffect(listOf(), effect)
