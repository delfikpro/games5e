package dev.implario.games5e.node;

import com.google.gson.JsonElement;

import java.util.UUID;

public interface GameCreator {

    Game createGame(UUID gameId, String imageId, JsonElement settings);

}
