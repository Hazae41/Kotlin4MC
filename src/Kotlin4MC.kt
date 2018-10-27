@file:JvmName("Kotlin4MC")

package fr.rhaz.minecraft.kotlin

import com.google.gson.JsonParser
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatColor.LIGHT_PURPLE
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.event.EventBus
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Material.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import java.io.*
import java.lang.reflect.Method
import java.net.URL
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer
import java.util.function.Consumer

//import net.md_5.bungee.api.plugin.Plugin as BungeePlugin
//import net.md_5.bungee.api.CommandSender as BungeeSender
//import net.md_5.bungee.api.plugin.Event as BungeeEvent
//import net.md_5.bungee.event.EventPriority as BungeeEventPriority
//import net.md_5.bungee.api.plugin.Listener as BungeeListener
//import net.md_5.bungee.event.EventHandler as BungeeEventHandler
//import net.md_5.bungee.config.Configuration as BungeeConfiguration
//import net.md_5.bungee.config.YamlConfiguration as BungeeYaml
//import net.md_5.bungee.config.ConfigurationProvider as BungeeConfigurationProvider
//import net.md_5.bungee.api.plugin.Command as BungeeCommand
//
//import org.bukkit.plugin.java.JavaPlugin as BukkitPlugin
//import org.bukkit.command.CommandSender as BukkitSender
//import org.bukkit.event.Event as BukkitEvent
//import org.bukkit.event.Listener as BukkitListener
//import org.bukkit.event.EventPriority as BukkitEventPriority
//import org.bukkit.event.EventHandler as BukkitEventHandler
//import org.bukkit.configuration.file.YamlConfiguration as BukkitYamlConfiguration
//import org.bukkit.command.CommandExecutor as BukkitCommandExecutor

// ----------------------------- TYPE ALIASES -----------------------------
typealias BungeePlugin = net.md_5.bungee.api.plugin.Plugin
typealias BungeeSender = net.md_5.bungee.api.CommandSender
typealias BungeeEvent = net.md_5.bungee.api.plugin.Event
typealias BungeeEventPriority = net.md_5.bungee.event.EventPriority
typealias BungeeListener = net.md_5.bungee.api.plugin.Listener
typealias BungeeEventHandler = net.md_5.bungee.event.EventHandler
typealias BungeeConfiguration = net.md_5.bungee.config.Configuration
typealias BungeeYaml = net.md_5.bungee.config.YamlConfiguration
typealias BungeeConfigurationProvider = net.md_5.bungee.config.ConfigurationProvider
typealias BungeeCommand = net.md_5.bungee.api.plugin.Command
typealias BungeeTask = net.md_5.bungee.api.scheduler.ScheduledTask

typealias BukkitPlugin = org.bukkit.plugin.java.JavaPlugin
typealias BukkitPluginCommand = org.bukkit.command.PluginCommand
typealias BukkitSender = org.bukkit.command.CommandSender
typealias BukkitEvent = org.bukkit.event.Event
typealias BukkitListener = org.bukkit.event.Listener
typealias BukkitEventPriority = org.bukkit.event.EventPriority
typealias BukkitEventHandler = org.bukkit.event.EventHandler
typealias BukkitYamlConfiguration = org.bukkit.configuration.file.YamlConfiguration
typealias BukkitCommandExecutor = org.bukkit.command.CommandExecutor
typealias BukkitConfigurationSection = org.bukkit.configuration.ConfigurationSection
typealias BukkitPlayer = org.bukkit.entity.Player

// ----------------------------- LOGGING -----------------------------
fun BukkitPlugin.info(msg: String) = logger.info(msg.replace("&", "§"))
fun BukkitPlugin.info(ex: Exception) { ex.message?.also(::info) }
fun BukkitPlugin.warning(msg: String) = logger.warning(msg.replace("&", "§"))
fun BukkitPlugin.warning(ex: Exception) { ex.message?.also(::warning) }
fun BukkitPlugin.severe(msg: String) = logger.severe(msg.replace("&", "§"))
fun BukkitPlugin.severe(ex: Exception) { ex.message?.also(::info) }
fun BukkitPlugin.log(ex: Exception) = log{ex.printStackTrace(this)}
fun BukkitPlugin.log(msg: String) = log{println(msg)}
val BukkitPlugin.log get() =
    dataFolder["log.txt"].apply { if(!exists()) createNewFile() }
fun BukkitPlugin.log(action: PrintWriter.() -> Unit) =
    PrintWriter(FileWriter(log, true), true)
    .apply{print(date); action()}.close()

fun BungeePlugin.info(msg: String) = logger.info(msg.replace("&", "§"))
fun BungeePlugin.info(ex: Exception) { ex.message?.also(::info) }
fun BungeePlugin.warning(msg: String) = logger.warning(msg.replace("&", "§"))
fun BungeePlugin.warning(ex: Exception) { ex.message?.also(::warning) }
fun BungeePlugin.severe(msg: String) = logger.severe(msg.replace("&", "§"))
fun BungeePlugin.severe(ex: Exception) { ex.message?.also(::severe) }
fun BungeePlugin.log(ex: Exception) = log{ex.printStackTrace(this)}
fun BungeePlugin.log(msg: String) = log{println(msg)}
val BungeePlugin.log get() =
    dataFolder["log.txt"].apply { if(!exists()) createNewFile() }
fun BungeePlugin.log(action: PrintWriter.() -> Unit) =
    PrintWriter(FileWriter(log, true), true)
    .apply{print(date); action()}.close()

// ----------------------------- KOTLIN4MC PLUGIN -----------------------------

class Kotlin4Bukkit: BukkitPlugin(){
    override fun onEnable() = update(58015, LIGHT_PURPLE)
}
class Kotlin4Bungee: BungeePlugin(){
    override fun onEnable() = update(58015, LIGHT_PURPLE)
}

// ----------------------------- JAVA COMPAT -----------------------------
val unit = Unit
fun <T> listener(callable: Consumer<T>): Function1<T, Unit> = { t -> callable.accept(t); Unit }
fun <T,U> listener(callable: BiConsumer<T, U>): Function2<T, U, Unit> = { t, u -> callable.accept(t, u); Unit }
fun <T,U,V> listener(callable: TriConsumer<T, U, V>): Function3<T, U, V, Unit> = { t, u, v -> callable.accept(t, u, v); Unit }
@FunctionalInterface
interface TriConsumer<T, U, V> {
    fun accept(t: T, u: U, v: V)

    fun andThen(after: TriConsumer<in T, in U, in V>): TriConsumer<T, U, V> {
        Objects.requireNonNull(after)
        return object: TriConsumer<T, U, V> {
            override fun accept(a: T, b: U, c: V) {
                accept(a, b, c)
                after.accept(a, b, c)
            }
        }
    }
}


// ----------------------------- OTHERS -----------------------------
val date: String get() = SimpleDateFormat("MMM dd yyyy HH:mm:ss").format(Date())
operator fun File.get(key: String) = File(this, key)
val String.lc get() = toLowerCase()
val String.ex get() = Exception(this)

fun <T> T.eq(other: T) = takeIf{it == other}
fun <T> T.not(other: T) = takeUnless{it == other}

val Any.unit get() = Unit
val Any.nul get() = null

fun ex(msg: String) = Exception(msg)

inline fun <reified T: Exception, reified U: Any> catch(
        err: (T) -> U,
        run: () -> U
): U = try{run()} catch(ex: Exception){
    if(ex is T) err(ex) else throw ex
}

inline fun <reified T: Exception> catch(
        err: (T) -> Unit = {it.printStackTrace()},
        run: () -> Unit
): Unit = catch<T, Unit>(err, run)

inline fun <reified T: Exception, reified U: Any> catch(
        default: U,
        run: () -> U
): U = try{run()} catch(ex: Exception){
    if(ex is T) default else throw ex
}

// ----------------------------- MESSAGING -----------------------------
fun text(string: String) = TextComponent(string.replace("&", "§"))

fun BungeeSender.msg(msg: String) = msg(text(msg))
fun BungeeSender.msg(text: TextComponent) = sendMessage(text)
fun BungeeSender.msg(ex: Exception) { ex.message?.also(::msg) }
fun BungeeSender.execute(cmd: String)
    = ProxyServer.getInstance().pluginManager.dispatchCommand(this, cmd)

fun BukkitSender.msg(text: TextComponent) = spigot().sendMessage(text)
fun BukkitSender.msg(msg: String) = msg(text(msg))
fun BukkitSender.msg(ex: Exception) { ex.message?.also(::msg) }
fun BukkitSender.execute(cmd: String)
    = Bukkit.dispatchCommand(this, cmd)


// ----------------------------- GUI -----------------------------

// ----------------------------- UPDATES CHECKER -----------------------------
fun spiget(id: Int, callback: (String) -> Unit) = Thread {
    try {
        val base = "https://api.spiget.org/v2/resources/"
        val conn = URL("$base$id/versions?size=100").openConnection()
        val json = InputStreamReader(conn.inputStream).let { JsonParser().parse(it).asJsonArray }
        callback(json.last().asJsonObject["name"].asString)
    } catch (e: IOException){}
}.start()

infix fun String.newerThan(v: String): Boolean = false.also{
    val s1 = split('.');
    val s2 = v.split('.');
    for(i in 0..Math.max(s1.size,s2.size)){
        if(i !in s1.indices) return false;
        if(i !in s2.indices) return true;
        if(s1[i].toInt() > s2[i].toInt()) return true;
        if(s1[i].toInt() < s2[i].toInt()) return false;
    }
}

// --- BUKKIT ---
fun BukkitPlugin.update(id: Int, color: ChatColor = LIGHT_PURPLE, permission: String = "rhaz.update")
        = spiget(id) here@{

    if (!(it newerThan description.version)) return@here;

    val url = "https://www.spigotmc.org/resources/$id"
    val message = text(
            "An update is available for ${description.name}!" +
                    " Download it here: $url"
    ).apply {
        this.color = color
        clickEvent = net.md_5.bungee.api.chat.ClickEvent(OPEN_URL, url)
    }

    schedule {
        server.consoleSender.msg(message)
    }

    listen<PlayerJoinEvent> {
        if (it.player.hasPermission(permission))
            it.player.msg(message)
    }
}

// --- BUNGEE ----
fun BungeePlugin.update(id: Int, color: ChatColor = LIGHT_PURPLE, permission: String = "rhaz.update")
        = spiget(id) here@{
    if (!(it newerThan description.version)) return@here;

    val url = "https://www.spigotmc.org/resources/$id"
    val message = text(
            "An update is available for ${description.name}!" +
                    " Download it here: $url"
    ).apply {
        this.color = color
        clickEvent = ClickEvent(OPEN_URL, url)
    }

    schedule {
        proxy.console.msg(message);
    }

    listen<PostLoginEvent> {
        if (it.player.hasPermission(permission))
            it.player.msg(message)
    }
}

// ----------------------------- CONFIG LOADING -----------------------------

// --- BUNGEE ---
val BungeePlugin.provider get() = BungeeConfigurationProvider.getProvider(BungeeYaml::class.java)
fun BungeePlugin.load(
        file: File,
        resource: String = file.nameWithoutExtension+"/bungee.yml"
) = try {
    if (!dataFolder.exists()) dataFolder.mkdir()
    if (!file.exists()) Files.copy(getResourceAsStream(resource), file.toPath())
    provider.load(file)
} catch (e: IOException){ e.printStackTrace(); null }
fun BungeePlugin.save(config: BungeeConfiguration, file: File) = provider.save(config, file)

fun BungeeConfiguration.section(path: String) = getSection(path)
val BungeeConfiguration.sections get() = keys.map{section(it)}

// --- BUKKIT ---
fun BukkitPlugin.load(
        file: File,
        resource: String = file.nameWithoutExtension+"/bukkit.yml"
): BukkitYamlConfiguration? {
    if (!file.parentFile.exists()) file.parentFile.mkdir()
    if (!file.exists()) java.nio.file.Files.copy(getResource(resource), file.toPath())
    return BukkitYamlConfiguration.loadConfiguration(file);
}

val BukkitYamlConfiguration.keys get() = getKeys(false)
fun BukkitYamlConfiguration.section(path: String) = getConfigurationSection(path)
val BukkitYamlConfiguration.sections get() = keys.map{section(it)}
val BukkitConfigurationSection.keys get() = getKeys(false)
fun BukkitConfigurationSection.section(path: String) = getConfigurationSection(path)
val BukkitConfigurationSection.sections get() = keys.map{section(it)}

// ----------------------------- LISTENERS -----------------------------
inline fun <reified T: BukkitEvent> BukkitPlugin.listen(
        priority: BukkitEventPriority = BukkitEventPriority.NORMAL,
        crossinline callback: (T) -> Unit
){
    server.pluginManager.registerEvent(
            T::class.java, object: BukkitListener {},
            priority, { _, it -> callback(it as T) },
            this
    )
}

inline fun <reified T: BungeeEvent> BungeePlugin.listen(
        priority: Byte = BungeeEventPriority.NORMAL,
        crossinline callback: (T) -> Unit
){
    val pm = proxy.pluginManager
    val pmc = pm::class.java
    val bus = pmc.getDeclaredField("eventBus").run {
        isAccessible = true; get(pm) as EventBus
    }
    val busc = bus::class.java
    val bLaP = busc.getDeclaredField("byListenerAndPriority").run {
        isAccessible = true; get(bus) as HashMap<Class<*>, Map<Byte, Map<Object, Array<Method>>>>
    }
    val priorities = bLaP[T::class.java] as? HashMap<Byte, Map<Object, Array<Method>>>
            ?: HashMap<Byte, Map<Object, Array<Method>>>()
                    .also { bLaP[T::class.java] = it }
    val handlers = priorities[priority] as? HashMap<Object, Array<Method>>
            ?: HashMap<Object, Array<Method>>()
                    .also { priorities[priority] = it }
    val listener = object: BungeeListener {
        fun onEvent(it: T) = callback(it)
    }
    handlers[listener as Object] = arrayOf(listener::class.java.getMethod("onEvent", BungeeEvent::class.java))
    busc.getDeclaredMethod("bakeHandlers", Class::class.java).apply {
        isAccessible = true; invoke(bus, T::class.java)
    }
    proxy.pluginManager.registerListener(this, listener)
}

// ----------------------------- COMMANDS -----------------------------

// -------------- BUNGEE --------------

// --- COMMAND AS RECEIVER ---
fun BungeePlugin.command(
        name: String,
        permission: String,
        vararg aliases: String,
        callback: BungeeCommand.(BungeeSender, Array<String>) -> Unit
) = object: BungeeCommand(name, permission, *aliases){
    override fun execute(sender: BungeeSender, args: Array<String>)
            = callback(sender, args)
}.also{ proxy.pluginManager.registerCommand(this, it) }

fun BungeePlugin.command(
        name: String,
        callback: BungeeCommand.(BungeeSender, Array<String>) -> Unit
) = object: BungeeCommand(name){
    override fun execute(sender: BungeeSender, args: Array<String>)
            = callback(sender, args)
}.also{ proxy.pluginManager.registerCommand(this, it) }

// --- SENDER AS RECEIVER ---
fun BungeePlugin.command(
        name: String,
        permission: String,
        vararg aliases: String,
        callback: BungeeSender.(Array<String>) -> Unit
) = command(name, permission, *aliases){sender, args -> sender.callback(args)}

fun BungeePlugin.command(
        name: String,
        callback: BungeeSender.(Array<String>) -> Unit
) = command(name){ sender, args -> sender.callback(args) }

// -------------- BUKKIT --------------

// --- COMMAND AS RECEIVER ---
fun BukkitPlugin.command(
        name: String, permission: String? = null, vararg aliases: String,
        executor: BukkitPluginCommand.(BukkitSender, Array<String>) -> Unit
) = getCommand(name).also {
    it.aliases = aliases.toList()
    it.executor = BukkitCommandExecutor {
        sender, _, _, args -> it.executor(sender, args)
        true
    }
    it.permission = permission ?: return@also
}

// --- SENDER AS RECEIVER ---
fun BukkitPlugin.command(
        name: String, permission: String? = null, vararg aliases: String,
        executor: BukkitSender.(Array<String>) -> Unit
) = command(name, permission, *aliases){ sender, args -> sender.executor(args)}

// ----------------------------- SCHEDULER -----------------------------

// --- BUKKIT ---
fun BukkitPlugin.schedule(
        async: Boolean = false,
        delay: Long? = null,
        period: Long? = null,
        unit: TimeUnit? = null,
        callback: BukkitTask.() -> Unit
): BukkitTask {
    lateinit var task: BukkitTask
    task =
    if(period != null){
        var delay = delay ?: 0
        delay = unit?.toSeconds(delay)?.let{it*20} ?: delay
        val period = unit?.toSeconds(period)?.let{it*20} ?: period
        if(async) server.scheduler.runTaskTimerAsynchronously(this, {task.callback()}, delay, period)
        else server.scheduler.runTaskTimer(this, {task.callback()}, delay, period)
    } else if(delay != null){
        val delay = unit?.toSeconds(delay)?.let{it*20} ?: delay
        if(async) server.scheduler.runTaskLaterAsynchronously(this, {task.callback()}, delay)
        else server.scheduler.runTaskLater(this, {task.callback()}, delay)
    } else if(async) server.scheduler.runTaskAsynchronously(this) {task.callback()}
    else server.scheduler.runTask(this) {task.callback()}
    return task
}
fun BukkitPlugin.cancelTasks() = server.scheduler.cancelTasks(this)

// --- BUNGEE ---
fun BungeePlugin.schedule(
        async: Boolean = false,
        delay: Long? = null,
        period: Long? = null,
        unit: TimeUnit? = null,
        callback: BungeeTask.() -> Unit
): BungeeTask {
    lateinit var task: BungeeTask
    task =  if(period != null){
        var delay = delay ?: 0
        val unit = unit ?: TimeUnit.MILLISECONDS.also{ delay *= 50 }
        if(async)
            proxy.scheduler.schedule(this, {
                proxy.scheduler.runAsync(this, {task.callback()})
            }, delay, period, unit)
        else proxy.scheduler.schedule(this, {task.callback()}, delay, period, unit)
    } else if(delay != null){
        var delay = delay
        val unit = unit ?: TimeUnit.MILLISECONDS.also{ delay *= 50 }
        return if(async)
            proxy.scheduler.schedule(this, {
                proxy.scheduler.runAsync(this) {task.callback()}
            }, delay, unit)
        else proxy.scheduler.schedule(this, {task.callback()}, delay, unit)
    } else if(async) proxy.scheduler.runAsync(this, {task.callback()})
    else proxy.scheduler.schedule(this, {task.callback()}, 0, TimeUnit.MILLISECONDS)
    return task
}
fun BungeePlugin.cancelTasks() = proxy.scheduler.cancel(this)
