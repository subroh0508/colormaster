package net.subroh0508.colormaster.api.firestore.serialization

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder

actual fun FirebaseDecoder.structureDecoder(descriptor: SerialDescriptor, decodeDouble: (value: Any?) -> Double?): CompositeDecoder = when(descriptor.kind as StructureKind) {
    StructureKind.CLASS, StructureKind.OBJECT -> (value as Map<*, *>).let { map ->
        FirebaseClassDecoder(decodeDouble, map.size, { map.containsKey(it) }) { desc, index -> map[desc.getElementName(index)] }
    }
    StructureKind.LIST -> (value as List<*>).let {
        FirebaseCompositeDecoder(decodeDouble, it.size) { _, index -> it[index] }
    }
    StructureKind.MAP -> (value as Map<*, *>).entries.toList().let {
        FirebaseCompositeDecoder(decodeDouble, it.size) { _, index -> it[index/2].run { if(index % 2 == 0) key else value }  }
    }
}
