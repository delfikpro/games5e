package implario.games5e.coordinator;

import com.google.inject.Inject;
import dev.implario.nettier.Nettier;
import dev.implario.nettier.NettierServer;
import dev.implario.nettier.RemoteException;
import implario.LoggerUtils;
import implario.games5e.packets.*;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.logging.Logger;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CoordinatorEndpoint {

    private final Balancer balancer;
    private final Map<UUID, RunningGame> players = new HashMap<>();

    public void start(int port) {

        Logger logger = LoggerUtils.simpleLogger("games5e-coordinator");

        NettierServer server = Nettier.createServer();

        server.setPacketTranslator((packet, expectedType) -> {
            if (packet instanceof PacketError) {
                throw new RemoteException(RemoteException.ErrorLevel.SEVERE, ((PacketError) packet).getMessage());
            }
            return packet;
        });

        server.addListener(PacketCreateGame.class, (talk, packet) -> {

            String imageId = packet.getGameInfo().getImageId();

            logger.info("Creating game " + packet.getGameInfo());

            GameNode node = balancer.getSufficientNode(imageId);

            if (node == null) {
                talk.respond(new PacketError("Unable to find a sufficient node for your game"));
                return;
            }

            // ToDo: maybe check if game with that id already exists

            node.startGame(packet.getGameInfo(), imageId).thenRun(() -> {
                talk.respond(new PacketOk("Game is ready"));
                // ToDo special packet with realm id
            });

            talk.respond(new PacketOk("Game created and is now initializing"));

        });

        server.addListener(PacketRequestGameStatus.class, (talk, packet) -> {

            RunningGame runningGame = balancer.getRunningGame(packet.getGameId());

            // ToDo: respond with TERMINATED state for games that existed in the past
            if (runningGame == null) talk.respond(new PacketError("No such game"));
            else talk.respond(new PacketGameStatus(runningGame.getInfo(), runningGame.getState()));

        });


        server.addListener(PacketNodeHandshakeV1.class, (talk, packet) -> {

            // ToDo: validate token
            logger.info("New node: " + talk.getRemote().getAddress());

            // ToDo: restore running games from packet.activeGames
            List<RunningGame> runningGames = new ArrayList<>();

            AbstractGameNode node = new AbstractGameNode(talk.getRemote(), runningGames, packet.getNodeType().getMatcher());

            balancer.addNode(node);
            talk.getRemote().setDisconnectHandler(() -> balancer.removeNode(node));

        });

        server.addListener(PacketRequestPlayerInfo.class, (talk, packet) -> {
            RunningGame game = players.get(packet.getPlayerId());
            UUID gameId = game == null ? null : game.getInfo().getGameId();
            talk.respond(new PacketPlayerInfo(packet.getPlayerId(), gameId));
        });

        server.start(port);
        logger.info("Started on port :" + port);

    }

}
