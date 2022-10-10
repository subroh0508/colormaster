package utilities

import io.ktor.client.plugins.*
import net.subroh0508.colormaster.presentation.common.external.I18nextText
import net.subroh0508.colormaster.presentation.common.external.invoke

fun buildErrorHeader(error: Throwable) = when (error) {
    is ResponseException -> "${error.response.status.value}: ${error.response.status.description}"
    else -> error::class.simpleName ?: "UnknownException"
}

fun buildErrorMessage(t: I18nextText, error: Throwable): String {
    if (error !is ResponseException) {
        return t("errors.unknown")
    }

    return when (error.response.status.value / 100) {
        4 -> t("errors.4xx")
        5 -> t("errors.5xx")
        else -> t("errors.unknown")
    }
}
