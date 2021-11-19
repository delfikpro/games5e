package dev.implario.games5e.node;

import com.google.gson.JsonElement;
import dev.implario.games5e.packets.*;
import dev.implario.nettier.Nettier;
import dev.implario.nettier.NettierClient;
import dev.implario.nettier.RemoteException;
import implario.Environment;
import dev.implario.games5e.GameInfo;
import dev.implario.games5e.Games5eGameState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Getter
@RequiredArgsConstructor
public class CoordinatorClient {

    private final GameNode node;

    @Setter
    private NettierClient client;

    private final Set<UUID> subscribedQueues = new HashSet<>();

    private List<PacketQueueState> allQueues = new ArrayList<>();

    public void enable() {

        if (this.client == null) this.client = Nettier.createClient();

        client.setPacketTranslator((packet, expectedType) -> {
            if (packet instanceof PacketError) {
                throw new RemoteException(RemoteException.ErrorLevel.SEVERE, ((PacketError) packet).getMessage());
            }
            return packet;
        });

        Plugin plugin = JavaPlugin.getProvidingPlugin(CoordinatorClient.class);
        client.setExecutor(task -> Bukkit.getScheduler().runTask(plugin, task));

        client.addListener(PacketCreateGame.class, (talk, packet) -> {

            GameInfo gameInfo = packet.getGameInfo();
            System.out.println("Starting game " + gameInfo);

            UUID gameId = gameInfo.getGameId();
            String imageId = gameInfo.getImageId();
            JsonElement settings = gameInfo.getSettings();

            try {
                Game game = node.createGame(gameId, imageId, settings);

                talk.respond(new PacketGameStatus(packet.getGameInfo(), game.getMeta(), Games5eGameState.INITIALIZING));

            } catch (Exception exception) {
                exception.printStackTrace();
                talk.respond(new PacketError("Unable to start game " + imageId + ": " + exception.getClass().getName() +
                        " " + exception.getMessage()));
            }

        });

        System.out.println("Connecting to games5e coordinator...");
        client.connect("127.0.0.1", Environment.requireInt("GAMES5E_COORDINATOR_PORT"));

        System.out.println("Connected!");

        client.addListener(PacketAllQueueStates.class, (talk, packet) -> {
            allQueues = packet.getStates();
        });

        client.setHandshakeHandler(r -> {
            r.send(new PacketNodeHandshakeV1("", node.getSupportedImagePrefixes(), new ArrayList<>(node.getRunningGames().keySet())));
            updateSubscribedQueues();
        });

    }

    public void updateSubscribedQueues() {
        client.send(new PacketSubscribedQueues(subscribedQueues));
    }

    public void subscribeToAllQueues() {
        subscribedQueues.clear();
        for (PacketQueueState queue : allQueues) {
            subscribedQueues.add(queue.getProperties().getQueueId());
        }
    }


}
