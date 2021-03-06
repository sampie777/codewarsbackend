package nl.sajansen.codewarsbackend.config

import org.slf4j.LoggerFactory
import java.lang.reflect.Field
import java.lang.reflect.Modifier

object Config {
    private val logger = LoggerFactory.getLogger(Config.toString())

    /* ********************************** */

    var gameStepsPerSecond = 10
    var boardWidth = 700
    var boardHeight = 600
    var boardDragCoefficient = 0.8f
    var playerDefaultSize = 30
    var playerFrictionConstant = 0.8f
    var playerEngineIdleFrictionConstant = 500f
    var playerDensity = 1.0f // 10e3 kg/m3
    var airDensity = 0.005f // 10e3 kg/m3
    var serverCheckDisconnectedTimeout = 1000L

    fun load() {
        try {
            PropertyLoader.load()
            PropertyLoader.loadConfig(this::class.java)
        } catch (e: Exception) {
            logger.error("Failed to load Config")
            e.printStackTrace()
        }
    }

    fun save() {
        try {
            if (PropertyLoader.saveConfig(this::class.java)) {
                PropertyLoader.save()
            }
        } catch (e: Exception) {
            logger.error("Failed to save Config")
            e.printStackTrace()
        }
    }

    fun get(key: String): Any? {
        try {
            return javaClass.getDeclaredField(key).get(this)
        } catch (e: Exception) {
            logger.error("Could not get config key $key")
            e.printStackTrace()
        }
        return null
    }

    fun set(key: String, value: Any?) {
        try {
            javaClass.getDeclaredField(key).set(this, value)
        } catch (e: Exception) {
            logger.error("Could not set config key $key")
            e.printStackTrace()
        }
    }

    fun enableWriteToFile(value: Boolean) {
        PropertyLoader.writeToFile = value
    }

    fun fields(): List<Field> {
        val fields = javaClass.declaredFields.filter {
            it.name != "INSTANCE" && it.name != "logger"
                    && Modifier.isStatic(it.modifiers)
        }
        fields.forEach { it.isAccessible = true }
        return fields
    }
}