package net.subroh0508.colormaster.components.core.external

external interface JsFunction<in C, out O> {
    fun call(ctx: C, vararg args: Any?): O
    fun apply(ctx: C, args: Array<out Any?>): O
    fun bind(ctx: C, vararg args: Any?): JsFunction<Nothing?, O>

    val length: Int
}

external interface JsFunction1<in I, out O> : JsFunction<Nothing?, O>

operator fun <I, O> JsFunction1<I, O>.invoke(arg: I) =
    asDynamic()(arg)
