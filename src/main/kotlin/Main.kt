import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
fun main() {
    File("/Users/fred/tmp/test_lsd.json").inputStream().use { fis ->
        val fileInfo = Json.decodeFromStream<FileInfo>(fis)
        println("Stored file at: ${fileInfo.fileContent}")
    }
}

@Serializable(with = LargeStringContentSerializer::class)
data class LargeStringContent (val fileName: String)

object LargeStringContentSerializer: KSerializer<LargeStringContent> {
    private val b64Decoder: Base64.Decoder = Base64.getDecoder()
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LargeStringContent", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LargeStringContent {
        var reminder = ""
        val decodedBytes = ByteArrayOutputStream().use { bos ->
            decoder.decodeStringChunked {
                val actualChunk = reminder + it
                val reminderLength = actualChunk.length % 4
                val alignedLength = actualChunk.length - reminderLength
                val alignedChunk = actualChunk.take(alignedLength)
                reminder = actualChunk.takeLast(reminderLength)

                println("Chunk size: ${actualChunk.length}")
                bos.write(b64Decoder.decode(alignedChunk))
            }
            bos.toByteArray()
        }
        File("/Users/fred/tmp/test_lsd.zip").outputStream().write(decodedBytes)

        return LargeStringContent("/Users/fred/tmp/test_lsd.zip")
    }

    override fun serialize(encoder: Encoder, value: LargeStringContent) {
        TODO("Not yet implemented")
    }
}

@Serializable
data class FileInfo(
    val fileName: String,
    val fileContent:LargeStringContent
)

