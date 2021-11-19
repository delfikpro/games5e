package dev.implario.games5e.node;

import com.google.gson.JsonElement;
import dev.implario.games5e.node.linker.BukkitLinker;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NoopGameNode implements GameNode {

    @Override
    public Game createGame(UUID gameId, String imageId, JsonElement settings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Game getGameByGameId(UUID gameId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getSupportedImagePrefixes() {
        return Collections.emptyList();
    }

    @Override
    public Map<UUID, Game> getRunningGames() {
        return Collections.emptyMap();
    }

    @Override
    public GameCreator getGameCreator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BukkitLinker getLinker() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<UUID, Game> getPlayerMap() {
        return Collections.emptyMap();
    }

    @Override
    public void setGameCreator(GameCreator gameCreator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLinker(BukkitLinker linker) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Game getGameByPlayerId(UUID playerId) {
        return null;
    }

    @Override
    public Game getGameByPlayer(Player player) {
        return null;
    }
}
