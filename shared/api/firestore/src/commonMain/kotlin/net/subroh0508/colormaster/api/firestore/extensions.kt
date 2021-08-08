package net.subroh0508.colormaster.api.firestore

import kotlinx.coroutines.channels.SendChannel

fun <E> SendChannel<E>.safeOffer(element: E) =
    runCatching { !isClosedForSend && trySend(element).isSuccess }.getOrDefault(false)

