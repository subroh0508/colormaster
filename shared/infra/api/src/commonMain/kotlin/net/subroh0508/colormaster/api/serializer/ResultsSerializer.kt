package net.subroh0508.colormaster.api.serializer

import kotlinx.serialization.*
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.SerialClassDescImpl

@Serializer(forClass = Response.Results::class)
internal class ResultsSerializer<T: Any>(
    private val bindingSerializer: KSerializer<T>
) : KSerializer<Response.Results<T>> {
    override val descriptor: SerialDescriptor = object : SerialClassDescImpl("Response.Results") {
        init { addElement("bindings") }
    }

    override fun deserialize(decoder: Decoder): Response.Results<T> {
        val inp = decoder.beginStructure(descriptor)

        val bindings: MutableList<T> = mutableListOf()

        do {
            val i = inp.decodeElementIndex(descriptor)

            if (i == 0) {
                bindings.addAll(inp.decodeSerializableElement(
                    descriptor,
                    i,
                    ArrayListSerializer(bindingSerializer)
                ))
            }
        } while (i != CompositeDecoder.READ_DONE && i != CompositeDecoder.UNKNOWN_NAME)
        inp.endStructure(descriptor)

        return Response.Results(bindings)
    }

    override fun serialize(encoder: Encoder, obj: Response.Results<T>) = Unit
}
