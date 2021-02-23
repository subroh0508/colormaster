package net.subroh0508.colormaster.api.imasparql.serializer

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializer(forClass = Response.Results::class)
internal class ResultsSerializer<T: Any>(
    private val bindingSerializer: KSerializer<T>
) : KSerializer<Response.Results<T>> {
    @kotlinx.serialization.InternalSerializationApi
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Response.Results") {
        element("bindings", bindingSerializer.descriptor)
    }

    @kotlinx.serialization.InternalSerializationApi
    override fun deserialize(decoder: Decoder): Response.Results<T> {
        val inp = decoder.beginStructure(descriptor)

        val bindings: MutableList<T> = mutableListOf()

        do {
            val i = inp.decodeElementIndex(descriptor)

            if (i == 0) {
                bindings.addAll(inp.decodeSerializableElement(
                    descriptor,
                    i,
                    ListSerializer(bindingSerializer)
                ))
            }
        } while (i != CompositeDecoder.DECODE_DONE && i != CompositeDecoder.UNKNOWN_NAME)
        inp.endStructure(descriptor)

        return Response.Results(bindings)
    }

    override fun serialize(encoder: Encoder, value: Response.Results<T>) = Unit
}
