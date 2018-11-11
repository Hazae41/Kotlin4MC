@file:JvmName("Kotlin4Bukkit")
@file:JvmMultifileClass

package fr.rhaz.minecraft.kotlin.bukkit

import fr.rhaz.minecraft.kotlin.ex
import fr.rhaz.minecraft.kotlin.get
import org.bukkit.Color
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.io.File
import kotlin.reflect.KProperty


@Deprecated("Use loadConfig() instead")
fun BukkitPlugin.load(file: File, resource: String = file.nameWithoutExtension + "/bukkit.yml") = loadConfig(file, resource)

@Deprecated("Replace with delegated configuration (ConfigFile)")
fun BukkitPlugin.loadConfig(
        file: File, resource: String = file.name
): BukkitYamlConfiguration {
    saveResource(resource, file)
    return BukkitYamlConfiguration.loadConfiguration(file)
            ?: throw fr.rhaz.minecraft.kotlin.ex("Could not load ${file.name}")
}

fun saveConfig(config: BukkitYamlConfiguration, file: File) = config.save(file)

fun BukkitPlugin.saveResource(resource: String, file: File) {
    if (file.exists()) return
    file.parentFile.mkdirs()
    file.createNewFile()
    getResource(resource).copyTo(file.outputStream())
}

fun BukkitPlugin.init(vararg configs: ConfigFile) {
    configs.forEach { it.init(this) }
}

abstract class Config(open var autoSave: Boolean = true) {

    private lateinit var _config: ConfigurationSection
    var config: ConfigurationSection
        set(value) {
            _config = value
        }
        get() {
            if (!::_config.isInitialized) reload()
            return _config
        }

    open val sections get() = config.sections

    abstract fun reload()
    abstract fun save()

    open inner class any(val path: String, val def: Any? = null) {
        operator fun getValue(ref: Any?, prop: KProperty<*>) = config.get(path, def)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: Any?) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class list(val path: String, val def: List<Any> = emptyList()) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<*> = config.getList(path)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Any>) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class boolean(val path: String, val def: Boolean = false) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): Boolean = config.getBoolean(path, def)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: Boolean) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class booleanList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Boolean> = config.getBooleanList(path)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Boolean>) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class string(val path: String, val def: String = "") {
        operator fun getValue(ref: Any?, prop: KProperty<*>): String = config.getString(path, def)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: String) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class stringList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<String> = config.getStringList(path)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<String>) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class int(val path: String, val def: Int = 0) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): Int = config.getInt(path, def)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: Int) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class intList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Int> = config.getIntegerList(path)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Int>) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class long(val path: String, val def: Long = 0L) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): Long = config.getLong(path, def)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: Long) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class longList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Long> = config.getLongList(path)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Long>) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class double(val path: String, val def: Double = 0.0) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): Double = config.getDouble(path, def)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: Double) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class doubleList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Double> = config.getDoubleList(path)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Double>) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class byteList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Byte> = config.getByteList(path)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Byte>) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class floatList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Float> = config.getFloatList(path)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Float>) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class shortList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Short> = config.getShortList(path)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Short>) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class charList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Char> = config.getCharacterList(path)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Char>) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class color(val path: String, val def: Color? = null) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): Color? = config.getColor(path, def)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: Color?) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class item(val path: String, val def: ItemStack? = null) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): ItemStack? = config.getItemStack(path, def)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: ItemStack?) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class offlinePlayer(val path: String, val def: OfflinePlayer? = null) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): OfflinePlayer? = config.getOfflinePlayer(path, def)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: OfflinePlayer?) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class vector(val path: String, val def: Vector? = null) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): Vector? = config.getVector(path, def)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: Vector?) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class serializable<T : ConfigurationSerializable>(val path: String, val clazz: Class<T>, val def: T? = null) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): T? = config.getSerializable(path, clazz, def)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: T?) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class mapList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Map<*, *>> = config.getMapList(path)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Map<*, *>>) {
            config.set(path, value); if (autoSave) save()
        }
    }

    open inner class section(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): ConfigurationSection? = config.getConfigurationSection(path)
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: ConfigurationSection?) {
            config.set(path, value); if (autoSave) save()
        }
    }
}

open class ConfigFile(autoSave: Boolean = true) : Config(autoSave) {

    lateinit var file: File

    constructor(
            file: File, autoSave: Boolean = true
    ) : this(autoSave) {
        this.file = file
    }

    private lateinit var path: String

    constructor(
            path: String, autoSave: Boolean = true
    ) : this(autoSave) {
        if (!path.endsWith(".yml"))
            this.path = "$path.yml"
        else this.path = path
    }

    fun init(plugin: BukkitPlugin, resource: String = path) {
        if (::file.isInitialized)
            throw ex("Config is already initialized")

        file = plugin.dataFolder[path]

        if (resource.endsWith(".yml"))
            plugin.saveResource(resource, file)
        else
            plugin.saveResource("$resource.yml", file)
    }

    override fun reload() {
        if (!::file.isInitialized) throw ex("Config is not initialized")
        config = BukkitYamlConfiguration.loadConfiguration(file)
                ?: throw ex("Could not load ${file.name}")
    }

    override fun save() {
        val config = config as? FileConfiguration
                ?: throw ex("Could not save ${config.name} to ${file.name}")
        config.save(file)
    }
}

open class ConfigSection(
        var parent: Config, var path: String, autoSave: Boolean = true
) : Config(autoSave) {

    override fun reload() {
        config = parent.config.getConfigurationSection(path)
                ?: throw ex("Could not load $path from ${parent.config.name}")
    }

    override fun save() = parent.config.set(path, config)
}