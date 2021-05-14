package implario.games.sdk;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@RequiredArgsConstructor(access = PROTECTED)
public abstract class Game {

    @Getter
    @Delegate
    private final GameContext context;

    public void start() {

    }

    public Location getSpawnLocation(UUID uuid) {
        return getMap().getWorld().getSpawnLocation();
    }

    public void end(GameResults gameResults) {

    }

    public void broadcast(String... messages) {
        for (Player player : context.getPlayers()) {
            player.sendMessage(messages);
        }
    }

}
