@file:JvmName("Kotlin4Bukkit")
@file:JvmMultifileClass

package fr.rhaz.minecraft.kotlin.bukkit

import fr.rhaz.minecraft.kotlin.catch
import fr.rhaz.minecraft.kotlin.newerThan
import fr.rhaz.minecraft.kotlin.spiget
import fr.rhaz.minecraft.kotlin.text
import kotlinx.coroutines.launch
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.TimeUnit

// PLUGIN
lateinit var kotlin4Bukkit: Kotlin4BukkitPlugin

class Kotlin4BukkitPlugin : BukkitPlugin() {
    init {
        kotlin4Bukkit = this
    }

    override fun onEnable() = update(58015, ChatColor.LIGHT_PURPLE)
}

// EVENTS
@JvmOverloads
inline fun <reified T : BukkitEvent> BukkitPlugin.listen(
        priority: BukkitEventPriority = BukkitEventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        crossinline callback: (T) -> Unit
) = server.pluginManager.registerEvent(
        T::class.java, object : BukkitListener {},
        priority, { _, it -> if (it is T) callback(it) },
        this, ignoreCancelled
)

// COMMANDS

// Command as receiver
fun BukkitPlugin.command(
        name: String, permission: String? = null, vararg aliases: String,
        executor: BukkitPluginCommand.(BukkitSender, Array<String>) -> Unit
) = getCommand(name).also {
    it.aliases = aliases.toList()
    it.executor = BukkitCommandExecutor { sender, _, _, args ->
        it.executor(sender, args)
        true
    }
    it.permission = permission ?: return@also
}

// Sender as receiver
fun BukkitPlugin.command(
        name: String, permission: String? = null, vararg aliases: String,
        executor: BukkitSender.(Array<String>) -> Unit
) = command(name, permission, *aliases) { sender, args -> sender.executor(args) }

// SCHEDULING
fun BukkitPlugin.schedule(
        async: Boolean = false,
        delay: Long? = null,
        period: Long? = null,
        unit: TimeUnit? = null,
        callback: BukkitTask.() -> Unit
): BukkitTask {
    lateinit var task: BukkitTask
    task =
            if (period != null) {
                var delay = delay ?: 0
                delay = unit?.toSeconds(delay)?.let { it * 20 } ?: delay
                val period = unit?.toSeconds(period)?.let { it * 20 } ?: period
                if (async) server.scheduler.runTaskTimerAsynchronously(this, { task.callback() }, delay, period)
                else server.scheduler.runTaskTimer(this, { task.callback() }, delay, period)
            } else if (delay != null) {
                val delay = unit?.toSeconds(delay)?.let { it * 20 } ?: delay
                if (async) server.scheduler.runTaskLaterAsynchronously(this, { task.callback() }, delay)
                else server.scheduler.runTaskLater(this, { task.callback() }, delay)
            } else if (async) server.scheduler.runTaskAsynchronously(this) { task.callback() }
            else server.scheduler.runTask(this) { task.callback() }
    return task
}

fun BukkitPlugin.cancelTasks() = server.scheduler.cancelTasks(this)

// UPDATES
@JvmOverloads
fun BukkitPlugin.update(
        id: Int,
        color: ChatColor = ChatColor.LIGHT_PURPLE,
        permission: String = "rhaz.update"
) = catch<Exception>(::log) {
    kotlinx.coroutines.GlobalScope.launch {

        val new = spiget(id)
                ?: throw Exception("Could not retrieve latest version")

        val old = description.version

        if (!(new newerThan old)) return@launch

        val url = "https://www.spigotmc.org/resources/$id"
        val message = text(
                "An update is available for ${description.name} ($old -> $new): $url"
        ).apply {
            this.color = color
            clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, url)
        }

        schedule { server.consoleSender.msg(message) }

        listen<PlayerJoinEvent> {
            if (it.player.hasPermission(permission))
                it.player.msg(message)
        }
    }
}