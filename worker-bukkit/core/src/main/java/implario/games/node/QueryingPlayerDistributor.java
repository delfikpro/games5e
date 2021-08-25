package implario.games.node;

import dev.implario.nettier.NettierRemote;
import implario.games.sdk.Game;
import implario.games5e.packets.PacketPlayerInfo;
import implario.games5e.packets.PacketRequestPlayerInfo;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

/**
 * SimplePlayerDistributor assumes that player can only
 * be in one game at any given time
 */
@RequiredArgsConstructor
public class QueryingPlayerDistributor implements PlayerDistributor {

    private final GameManager gameManager;
    private final NettierRemote remote;

    @Override
    public Game assignGame(UUID playerId) {

        PacketPlayerInfo info = remote.send(new PacketRequestPlayerInfo(playerId)).await(PacketPlayerInfo.class);
        return gameManager.getGame(info.getGameId());

    }

}
