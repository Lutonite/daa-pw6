package ch.heigvd.iict.daa.rest.rest

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Helper Retrofit converter factory to convert additional types that Gson does not support.
 *
 * @author Emilie Bressoud
 * @author Loïc Herman
 * @author Sacha Butty
 */
object ConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? = when (type) {
        UUID::class.java -> UUIDResponseConverter
        else -> null
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? = when (type) {
        UUID::class.java -> UUIDRequestConverter
        else -> null
    }

    override fun stringConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? = when (type) {
        UUID::class.java -> StringUUIDConverter
        else -> null
    }
}

/**
 * UUID to String converter
 *
 * @author Emilie Bressoud
 * @author Loïc Herman
 * @author Sacha Butty
 */
object StringUUIDConverter : Converter<UUID, String> {
    override fun convert(value: UUID): String {
        return value.toString()
    }
}

/**
 * UUID response converter
 *
 * @author Emilie Bressoud
 * @author Loïc Herman
 * @author Sacha Butty
 */
object UUIDResponseConverter : Converter<ResponseBody, UUID> {
    override fun convert(value: ResponseBody): UUID {
        return UUID.fromString(value.string())
    }
}

/**
 * UUID request converter
 *
 * @author Emilie Bressoud
 * @author Loïc Herman
 * @author Sacha Butty
 */
object UUIDRequestConverter : Converter<UUID, RequestBody> {
    private val MEDIA_TYPE: MediaType = MediaType.get("text/plain; charset=UTF-8")

    override fun convert(value: UUID): RequestBody {
        return RequestBody.create(MEDIA_TYPE, value.toString())
    }
}

/**
 * Gson adapter for OffsetDateTime
 *
 * @author Emilie Bressoud
 * @author Loïc Herman
 * @author Sacha Butty
 */
object OffsetDateTimeAdapter : JsonSerializer<OffsetDateTime>, JsonDeserializer<OffsetDateTime> {

    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    override fun serialize(
        src: OffsetDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement = JsonPrimitive(src?.format(formatter))

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): OffsetDateTime = OffsetDateTime.parse(json?.asString, formatter)
}

