package dev.implario.games5e.node.util;

import dev.implario.bukkit.event.EventContext;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.function.Consumer;

@UtilityClass
public class EventContextUtil {

    private final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    @SneakyThrows
    public void registerEvents(EventContext context, Listener listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                method.setAccessible(true);
                EventHandler handler = method.getAnnotation(EventHandler.class);
                MethodHandle methodHandle = LOOKUP.unreflect(method).bindTo(listener);
                Consumer<Event> consumer = MethodHandleProxies.asInterfaceInstance(Consumer.class, methodHandle);
                context.on((Class<Event>) methodHandle.type().parameterType(0), handler.priority(), event -> {
                    if (event instanceof Cancellable) {
                        Cancellable cancellable = (Cancellable) event;
                        if (cancellable.isCancelled() && handler.ignoreCancelled()) {
                            return;
                        }
                    }

                    consumer.accept(event);
                });
            }
        }
    }
}
