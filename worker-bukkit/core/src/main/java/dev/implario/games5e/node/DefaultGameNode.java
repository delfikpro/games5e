package dev.implario.games5e.node;

import com.google.gson.JsonElement;
import dev.implario.games5e.node.linker.BukkitLinker;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class DefaultGameNode implements GameNode {

    private final List<String> supportedImagePrefixes = new ArrayList<>();

    private final Map<UUID, Game> runningGames = new HashMap<>();

    private GameCreator gameCreator;

    @Delegate
    private BukkitLinker linker;

    @Override
    public Game createGame(UUID gameId, String imageId, JsonElement settings) {

        Game game = gameCreator.createGame(gameId, imageId, settings);
        if (game == null) {
            throw new IllegalArgumentException("Creator " + gameCreator.getClass() +
                    " refused to create game '" + imageId + "'");
        }
        game.getContext().appendOption(event -> event instanceof PlayerLoginEvent &&
                getGameByPlayer(((PlayerLoginEvent) event).getPlayer()) == game);
        runningGames.put(gameId, game);
        return game;

    }

    @Override
    public Game getGameByGameId(UUID gameId) {
        return runningGames.get(gameId);
    }

}
