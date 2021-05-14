package implario.games.sdk.util

import clepto.bukkit.event.EventContextProxy
import org.bukkit.event.Event
import org.bukkit.event.EventPriority

inline fun <reified T : Event> EventContextProxy.on(
    priority: EventPriority = EventPriority.NORMAL,
    noinline handler: T.() -> Unit
) = on(T::class.java, priority, handler)

inline fun <reified T : Event> EventContextProxy.after(noinline handler: T.() -> Unit) =
    on(priority = EventPriority.HIGH, handler)

inline fun <reified T : Event> EventContextProxy.before(noinline handler: T.() -> Unit) =
    on(priority = EventPriority.LOW, handler)

