package dev.implario.games5e.node;

import com.google.gson.JsonElement;
import dev.implario.games5e.node.linker.BukkitLinker;

import java.util.UUID;

public interface GameNode {
    Game createGame(UUID gameId, String imageId, JsonElement settings);

    Game getGameByGameId(UUID gameId);

    java.util.List<String> getSupportedImagePrefixes();

    java.util.Map<UUID, Game> getRunningGames();

    GameCreator getGameCreator();

    BukkitLinker getLinker();

    java.util.Map<UUID, Game> getPlayerMap();

    void setGameCreator(GameCreator gameCreator);

    void setLinker(BukkitLinker linker);

    Game getGameByPlayerId(UUID playerId);

    Game getGameByPlayer(org.bukkit.entity.Player player);
}
