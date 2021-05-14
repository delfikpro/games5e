package implario.games.node;

import clepto.bukkit.B;
import clepto.bukkit.event.EventContext;
import clepto.bukkit.routine.BukkitDoer;
import clepto.bukkit.world.Area;
import clepto.bukkit.world.Label;
import com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import implario.games.node.loader.BadImageException;
import implario.games.node.loader.GameImage;
import implario.games.node.loader.GameInstanceImpl;
import implario.games.node.loader.GameLoader;
import implario.games.node.test.SimpleArea;
import implario.games.sdk.Game;
import implario.games.sdk.GameContext;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.io.File;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class GameNode extends JavaPlugin {

    private final GameManager gameManager = new GameManager();

    @Setter
    private PlayerDistributor playerDistributor = new SimplePlayerDistributor(gameManager);

    public int localGameIndex = 0;

    @Override
    public void onEnable() {
        B.plugin = this;


        getCommand("game").setExecutor((sender, a, b, args) -> {
            String msg = execute(sender instanceof Player ? (Player) sender : null, args);
            sender.sendMessage(msg);
            return true;
        });


        CommandExecutor gcommands = (sender, a, b, args) -> {
            String command = b.equals("g1") ? "game create game-example 8a660e94-b6aa-37cf-85f5-557a4d6a7965" :
                    "game create game-example f475d676-94f7-351d-9064-ee66e0d66b9e";
            Bukkit.dispatchCommand(sender, command);
            return true;
        };

        getCommand("g1").setExecutor(gcommands);
        getCommand("g2").setExecutor(gcommands);

        EventContext globalContext = new EventContext(e -> true);

        globalContext.on(PlayerJoinEvent.class, e -> {
            e.getPlayer().sendMessage("§eUUID: §f" + e.getPlayer().getUniqueId());
        });
        globalContext.on(AsyncPlayerPreLoginEvent.class, EventPriority.LOW, e -> {
            Game game = playerDistributor.assignGame(e.getUniqueId());

            if (game == null) {
                e.setKickMessage("No game found for you on this server.");
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
                return;
            }

        });

        try {
            Class.forName("com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent");
            globalContext.on(PlayerInitialSpawnEvent.class, e -> {
                Location loc = gameManager.getGame(e.getPlayer()).getSpawnLocation(e.getPlayer().getUniqueId());
                e.setSpawnLocation(loc);
            });
        } catch (ClassNotFoundException ignored) {
        }

        globalContext.on(PlayerSpawnLocationEvent.class, e -> {

            Game game = playerDistributor.assignGame(e.getPlayer().getUniqueId());

            game.getPlayers().add(e.getPlayer());

            Location loc = game.getSpawnLocation(e.getPlayer().getUniqueId());
            e.setSpawnLocation(loc);
        });

        globalContext.on(PlayerRespawnEvent.class, EventPriority.LOWEST, e -> {

            Game game = playerDistributor.assignGame(e.getPlayer().getUniqueId());

            Location loc = game.getSpawnLocation(e.getPlayer().getUniqueId());
            e.setRespawnLocation(loc);

        });

        globalContext.on(PlayerJoinEvent.class, e -> {

            e.setJoinMessage("");
            UUID uuid = e.getPlayer().getUniqueId();
            Game game = playerDistributor.assignGame(uuid);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!game.getAssignedPlayers().contains(onlinePlayer.getUniqueId())) {
                    e.getPlayer().hidePlayer(this, onlinePlayer);
                    onlinePlayer.hidePlayer(this, e.getPlayer());
                }
            }
        });

        globalContext.on(PlayerQuitEvent.class, e -> {

            e.setQuitMessage("");

        });

        globalContext.on(PlayerDeathEvent.class, e -> {
            e.setDeathMessage("");
        });

        globalContext.on(AsyncPlayerChatEvent.class, EventPriority.LOW, e -> {

            UUID uuid = e.getPlayer().getUniqueId();
            Game game = playerDistributor.assignGame(uuid);
            e.getRecipients().removeIf(p -> !game.getAssignedPlayers().contains(p.getUniqueId()));

        });

    }

    private String execute(Player sender, String[] args) {

        if (args[0].equals("create") && args.length >= 2) {

            String fileName = args[1];

            if (!fileName.matches("^[a-zA-Z0-9_-]+$"))
                return "§cIllegal image name";

            GameImage image;

            try {
                image = GameLoader.load(new File("images/" + fileName + ".jar"));
            } catch (BadImageException e) {
                e.printStackTrace();
                return "§cBad image!";
            }

            UUID gameId = UUID.randomUUID();

            WorldCreator creator = new WorldCreator("tmp/" + gameId);
            creator.generateStructures(false);
            creator.type(WorldType.FLAT);

            World world = Bukkit.createWorld(creator);
            Area area = new SimpleArea(gameId.toString(), (CraftWorld) world);
            area.getLabels().add(new Label("spawn", "red", world, 10, 10, 0));
            area.getLabels().add(new Label("spawn", "blue", world, -10, 10, 0));

            Gson gson = new Gson();
            JsonElement gameSettings = gson.fromJson(getTextResource("bedwars.json"), JsonElement.class);

            List<UUID> assignedPlayers = gson.fromJson(gameSettings, BedWarsSettings.class).teams.stream()
                    .flatMap(t -> t.assignedPlayers.stream()).collect(Collectors.toList());


            GameContext instance = new GameInstanceImpl(
                    new EventContext(e -> true),
                    new BukkitDoer(this),
                    area,
                    gameId,
                    new ArrayList<>(),
//                    assignedPlayers,
                    Arrays.stream(args).skip(2).map(UUID::fromString).collect(Collectors.toList()),
                    ++localGameIndex
            );

            try {
                gameManager.createGame(image, instance, gameSettings);
                return "§aSuccess.";
            } catch (Exception e) {
                e.printStackTrace();
                return "§cError while preparing the game!";
            }

        }

        return "Usage: /game create [image]";

    }
}

class TeamSettings {
    List<UUID> assignedPlayers;
}
class BedWarsSettings {
    List<TeamSettings> teams;
}
