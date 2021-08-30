package implario.games.node;

import com.google.gson.JsonObject;
import dev.implario.bukkit.event.EventContext;
import dev.implario.nettier.Nettier;
import dev.implario.nettier.NettierClient;
import dev.implario.nettier.RemoteException;
import dev.implario.nettier.Talk;
import implario.Environment;
import implario.games.node.loader.BadImageException;
import implario.games.node.loader.GameImage;
import implario.games.node.loader.GameInstanceImpl;
import implario.games.node.loader.ImageLoader;
import implario.games.node.loader.download.ImageProvider;
import implario.games.node.loader.download.MavenImageProvider;
import implario.games.sdk.GameContext;
import implario.games5e.GameInfo;
import implario.games5e.NodeType;
import implario.games5e.packets.PacketCreateGame;
import implario.games5e.packets.PacketError;
import implario.games5e.packets.PacketNodeHandshakeV1;
import implario.games5e.packets.PacketOk;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Getter
public class GameNode extends JavaPlugin {

    private final GameManager gameManager = new GameManager();

    @Setter
    private PlayerDistributor playerDistributor;

    private final List<ImageProvider> imageProviders = new ArrayList<>();

    public int localGameIndex = 0;

    @Override
    public void onEnable() {
        imageProviders.add(new MavenImageProvider());

        WorkerBukkitAdapter.init(this);

        NettierClient client = Nettier.createClient();

//        client.setExecutor(r -> Bukkit.getScheduler().runTask(this, r));
        getCommand("game").setExecutor((sender, a, b, args) -> {

            sender.sendMessage("Creating test game...");
            try {
                Talk talk = client.send(new PacketCreateGame(new GameInfo(
                        UUID.randomUUID(), null,
                        "bukkit-maven https://repo.implario.dev/cristalix ru.cristalix duels 1.0.0-SNAPSHOT",
                        System.currentTimeMillis(), new JsonObject()
                )));
                talk.awaitFuture(PacketOk.class).thenCompose(t -> talk.awaitFuture(PacketOk.class)).thenAccept(m ->
                        System.out.println(m.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        });

        client.setPacketTranslator((packet, expectedType) -> {
            if (packet instanceof PacketError) {
                throw new RemoteException(RemoteException.ErrorLevel.SEVERE, ((PacketError) packet).getMessage());
            }
            return packet;
        });

        client.addListener(PacketCreateGame.class, (talk, packet) -> {

            System.out.println("Starting game " + packet.getGameInfo());

            String imageId = packet.getGameInfo().getImageId();

            File imageFile = null;

            for (ImageProvider imageProvider : imageProviders) {

                File file;
                try {
                    file = imageProvider.provideImage(imageId);

                    // If this ImageProvider doesn't support this image, it will return null
                    if (file != null) {
                        imageFile = file;
                        break;
                    }

                } catch (Exception exception) {
                    exception.printStackTrace();
                    talk.respond(new PacketError("Unable to get image " + imageId + ": " + exception.getClass().getName() +
                            " " + exception.getMessage()));
                    return;
                }

            }

            if (imageFile == null) {
                talk.respond(new PacketError("This node doesn't support image " + imageId));
                return;
            }

            GameImage image;
            try {
                image = ImageLoader.load(imageFile);
            } catch (BadImageException e) {
                e.printStackTrace();
                talk.respond(new PacketError("Failed to load image " + imageId + ": " + e.getMessage()));
                return;
            }

            GameContext instance = new GameInstanceImpl(
                    new EventContext(e -> true),
                    packet.getGameInfo().getGameId(),
                    new ArrayList<>(),
                    ++localGameIndex
            );

            Bukkit.getScheduler().runTask(this, () -> {
                try {
                    gameManager.createGame(image, instance, packet.getGameInfo().getSettings());
                } catch (Exception e) {
                    e.printStackTrace();
                    talk.respond(new PacketError("error while initializing game: " + e.getClass().getSimpleName() + " " + e.getMessage()));
                    return;
                }
                talk.respond(new PacketOk("Game " + packet.getGameInfo().getGameId() + " is ready"));
            });


        });

        playerDistributor = new QueryingPlayerDistributor(gameManager, client);

        System.out.println("Connecting...");
        client.connect("127.0.0.1", Environment.requireInt("GAMES5E_MINDER_PORT"));
        System.out.println("Connected!");

        client.setHandshakeHandler(r -> {
            r.send(new PacketNodeHandshakeV1("", NodeType.BUKKIT, new ArrayList<>(gameManager.getRunningGames().keySet())));
        });

    }

}

