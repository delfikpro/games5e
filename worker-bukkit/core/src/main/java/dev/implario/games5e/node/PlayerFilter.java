package dev.implario.games5e.node;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

@FunctionalInterface
public interface PlayerFilter {

    boolean acceptPlayer(AsyncPlayerPreLoginEvent event);

}
