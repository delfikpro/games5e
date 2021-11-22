package dev.implario.games5e.node;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;

@Getter
@Setter
@RequiredArgsConstructor
public class Teams<T> {

    private final List<T> teams;

    private final Map<UUID, T> assignationMap = new HashMap<>();
    private final Map<T, Set<UUID>> assignations = new IdentityHashMap<>();

    private final Map<UUID, T> playerMap = new HashMap<>();
    private final Map<T, Set<Player>> realPlayers = new IdentityHashMap<>();

    private Function<Teams<T>, T> autoAssigner = Teams::getLeastAssignations;

    public Teams() {
        this(new ArrayList<>());
    }

    public T getAssignation(UUID playerId) {
        return assignationMap.get(playerId);
    }

    public T getTeam(Player player) {
        return getTeam(player.getUniqueId());
    }

    public T getTeam(UUID playerId) {
        return playerMap.get(playerId);
    }

    public T ensureHasAssignation(UUID playerId) {
        T team = assignationMap.get(playerId);
        if (team != null) return team;
        team = autoAssigner.apply(this);
        assignationMap.put(playerId, team);
        assignations.computeIfAbsent(team, (k) -> new HashSet<>()).add(playerId);
        return team;
    }

    public T getLeastAssignations() {
        T minTeam = null;
        int minAmount = 0;
        for (Map.Entry<T, Set<UUID>> entry : assignations.entrySet()) {
            int amount = entry.getValue().size();
            if (minTeam == null || amount < minAmount) {
                minAmount = amount;
                minTeam = entry.getKey();
            }
        }
        return minTeam;
    }

    public T addPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        T team = ensureHasAssignation(playerId);
        removePlayer(player);
        playerMap.put(playerId, team);
        getPlayers(team).add(player);
        return team;
    }

    public void removePlayer(Player player) {
        realPlayers.values().forEach(v -> v.remove(player));
        playerMap.remove(player.getUniqueId());
    }

    public void removeAssignation(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) removePlayer(player);
        assignationMap.remove(uuid);
        assignations.values().forEach(v -> v.remove(uuid));
    }

    public boolean sameTeam(Player p1, Player p2) {
        return getTeam(p1) == getTeam(p2);
    }

    public Set<Player> getPlayers(T team) {
        return realPlayers.computeIfAbsent(team, (k) -> new HashSet<>());
    }

}
