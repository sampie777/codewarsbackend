package nl.sajansen.codewarsbackend

import com.google.gson.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.websocket.*
import nl.sajansen.codewarsbackend.config.Config
import nl.sajansen.codewarsbackend.utils.getCurrentJarDirectory
import nl.sajansen.codewarsbackend.ws.websocketRoutes
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.lang.reflect.Type
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val logger = LoggerFactory.getLogger("Application")

fun main(args: Array<String>) {
    return io.ktor.server.netty.EngineMain.main(args)
}

@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val logger = LoggerFactory.getLogger("Application")
    ApplicationRuntimeSettings.testing = testing

    logger.info("Starting application ${ApplicationInfo.artifactId}:${ApplicationInfo.version}")
    logger.info("Executing JAR directory: " + getCurrentJarDirectory(ApplicationInfo).absolutePath)
    if (testing) {
        logger.info("Testing mode is true")
    }

    Config.enableWriteToFile(!ApplicationRuntimeSettings.virtualConfig && !testing)
    Config.load()
    Config.save()

    install(Locations)

    install(ContentNegotiation) {
        gson {
            val dateTimeFormatter = "yyyy-MM-dd'T'HH:mm:ss.SSS"
            setDateFormat(dateTimeFormatter)

            // Add serializer for LocalDateTime to string
            registerTypeAdapter(LocalDateTime::class.java, object : JsonSerializer<LocalDateTime> {
                override fun serialize(
                    value: LocalDateTime,
                    type: Type,
                    context: JsonSerializationContext
                ): JsonElement {
                    val formatter = DateTimeFormatter.ofPattern(dateTimeFormatter)
                    return JsonPrimitive(value.format(formatter))
                }
            })

            // Add serializer for string to LocalDateTime
            registerTypeAdapter(LocalDateTime::class.java, object : JsonDeserializer<LocalDateTime?> {
                override fun deserialize(
                    value: JsonElement,
                    type: Type,
                    context: JsonDeserializationContext
                ): LocalDateTime? {
                    if (value.asJsonPrimitive.asString.isNullOrEmpty()) {
                        return null
                    }
                    val formatter = DateTimeFormatter.ofPattern(dateTimeFormatter)
                    // Remove Z from time to convert it to Local time.
                    return LocalDateTime.parse(value.asJsonPrimitive.asString.replace("Z", ""), formatter)
                }
            })
        }
    }

    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header(HttpHeaders.ContentType)
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(DataConversion)

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        websocketRoutes()
    }
}
