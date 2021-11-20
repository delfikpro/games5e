package dev.implario.games5e.node;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class GameTerminateEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Game game;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
