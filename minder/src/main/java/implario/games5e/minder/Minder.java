package implario.games5e.minder;

import dev.implario.nettier.Nettier;
import dev.implario.nettier.NettierServer;
import implario.Environment;
import implario.LoggerUtils;
import implario.games5e.speech.PacketCreateGame;
import implario.games5e.speech.PacketError;
import implario.games5e.speech.PacketNodeInfo;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class Minder {

    private final List<GameNode> nodes = new ArrayList<>();

    public void start(int port) {

        Logger logger = LoggerUtils.simpleLogger("games5e-minder");

        NettierServer server = Nettier.createServer();

        server.addListener(PacketCreateGame.class, (talk, packet) -> {

            GameNode node = Collections.min(nodes, Comparator.comparingInt(n -> n.getRunningGames().size()));
            if (node == null) {
                talk.respond(new PacketError("Unable to find a node for your game."));
                return;
            }

            long time = System.currentTimeMillis();
            MindedGame game = new MindedGame(packet.getGameId(), time, packet.getPlayers());

            node.getRunningGames().add(game);
            node.getRemote().send(packet);

        });

        server.addListener(PacketNodeInfo.class, (talk, packet) -> {
            logger.info("New node: " + talk.getRemote().getAddress());
            nodes.add(new GameNode(talk.getRemote(), new ArrayList<>()));
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

        new Minder().start(port);

    }

}
