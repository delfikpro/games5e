package implario.games5e.coordinator;

import com.google.inject.Inject;
import dev.implario.nettier.Nettier;
import dev.implario.nettier.NettierServer;
import implario.Environment;
import implario.LoggerUtils;
import implario.games5e.packets.*;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.RequiredArgsConstructor;
import ru.cristalix.core.stats.impl.network.packet.PacketAuth;
import ru.cristalix.core.stats.impl.network.packet.PacketOk;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CoordinatorEndpoint {

//    private final List<GameNode> nodes = new ArrayList<>();

    private final GameHistory history;
    private final Balancer balancer;
    private final ImageRegistry registry;

    public void start(int port) {

        Logger logger = LoggerUtils.simpleLogger("games5e-minder");

        NettierServer server = Nettier.createServer();

        server.addListener(PacketCreateGame.class, (talk, packet) -> {

            Image image = registry.getImage(packet.getGameInfo().getImageId());

            if (image == null) {
                talk.respond(new PacketError("Unknown image"));
                return;
            }

            GameNode node = balancer.getSufficientNode(image);

            if (node == null) {
                talk.respond(new PacketError("Unable to find a sufficient node for your game"));
                return;
            }

            // ToDo: maybe check if game with that id already exists

            node.startGame(packet.getGameInfo(), image);
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

            AbstractGameNode node = new AbstractGameNode(talk.getRemote(), runningGames, packet.getNodeType());

            balancer.addNode(node);

        });

        server.start(port);

    }

    public static void main(String[] args) {

        OptionParser optionParser = new OptionParser(true);
        OptionSpec<Integer> portSpec = optionParser.accepts("port").withRequiredArg()
                .defaultsTo(Environment.get("GAMES5E_MINDER_PORT")).ofType(Integer.class);

        OptionSet options = optionParser.parse(args);

        Integer port = portSpec.value(options);
        if (port == null) {
            System.out.println("Please specify the port either with --port option or " +
                    "with GAMES5E_MINDER_PORT environment variable");
            return;
        }

        new CoordinatorEndpoint().start(port);

    }

}
