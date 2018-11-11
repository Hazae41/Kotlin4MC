@file:JvmName("Kotlin4Nukkit")
@file:JvmMultifileClass

package fr.rhaz.minecraft.kotlin.nukkit

lateinit var kotlin4Nukkit: Kotlin4NukkitPlugin

class Kotlin4NukkitPlugin : NukkitPlugin() {
    init {
        kotlin4Nukkit = this
    }
}

@JvmOverloads
inline fun <reified T : NukkitEvent> NukkitPlugin.listen(
        priority: NukkitEventPriority = NukkitEventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        crossinline callback: (T) -> Unit
) = server.pluginManager.registerEvent(
        T::class.java, object : NukkitListener {},
        priority, { _: NukkitListener, it: NukkitEvent -> if (it is T) callback(it) },
        this, ignoreCancelled
)
