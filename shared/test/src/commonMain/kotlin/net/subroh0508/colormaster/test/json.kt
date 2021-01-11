package net.subroh0508.colormaster.test

fun resultJson(block: () -> String) = "{\"head\":{\"vars\":[\"id\",\"name\",\"color\"]},\"results\":{\"bindings\":[${block()}]}}"

fun jsonIdolColor(
    lang: String,
    id: String,
    name: String,
    color: String,
) = "{\"id\":{\"type\":\"literal\",\"value\":\"$id\"},\"name\":{\"type\":\"literal\",\"xml:lang\":\"$lang\",\"value\":\"$name\"},\"color\":{\"type\":\"literal\",\"datatype\":\"http://www.w3.org/2001/XMLSchema#hexBinary\",\"value\":\"$color\"}}"

