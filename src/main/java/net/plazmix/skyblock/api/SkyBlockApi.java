package net.plazmix.skyblock.api;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import net.plazmix.skyblock.api.advancement.SkyAdvancementManager;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.skyblock.api.player.SkyPlayer;
import net.plazmix.skyblock.api.util.IslandUtil;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class SkyBlockApi {

    @Setter
    private SkyBlock plugin;

    private final Map<String, SkyPlayer> playerMap          = new HashMap<>();
    private final SkyAdvancementManager advancementManager  = new SkyAdvancementManager();

    @Getter
    private static final SkyBlockApi instance = new SkyBlockApi();


    public SkyPlayer getPlayer(@NonNull String playerName) {
        return playerMap.computeIfAbsent(playerName.toLowerCase(), f -> new SkyPlayer(playerName));
    }

    public SkyPlayer getPlayer(@NonNull Player player) {
        return getPlayer(player.getName());
    }

    public SkyIsland getIsland(@NonNull String playerName) {
        SkyPlayer skyPlayer = playerMap.values().stream().filter(skyPlayer1 -> {

            SkyIsland skyIsland = skyPlayer1.island;
            if (skyIsland == null) return false;

            return skyPlayer1.getName().equalsIgnoreCase(playerName) || skyIsland.isMember(playerName);

        }).findFirst().orElse(null);


        if (skyPlayer == null) {
            return null;
        }

        return skyPlayer.island;
    }

    public SkyIsland getIsland(@NonNull SkyPlayer skyPlayer) {
        return getIsland(skyPlayer.getName());
    }

    public SkyIsland getIsland(@NonNull Location location) {
        SkyPlayer player = playerMap.values()
                .stream()
                .filter(skyPlayer -> {

            SkyIsland skyIsland = (skyPlayer.island);

            if (skyIsland == null) {
                return false;
            }

            Location islandLocation = IslandUtil.getIslandLocation(skyIsland);

            if (islandLocation == null) {
                return false;
            }

            if (!islandLocation.getWorld().getName().equals(location.getWorld().getName())) {
                return false;
            }

            return (islandLocation.distance(location) <= 300);
        }).findFirst().orElse(null);

        return player == null ? null : player.island;
    }

    public boolean hasIsland(@NonNull String playerName) {
        return getIsland(playerName) != null;
    }

    public boolean hasIsland(@NonNull SkyPlayer skyPlayer) {
        return hasIsland(skyPlayer.getName());
    }

    public Path getPlayerIslandPath(@NonNull String playerName) {
        return plugin.getDataFolder().toPath().resolve(playerName.toLowerCase().concat(".yml"));
    }

    public World getIslandWorld() {
        return Bukkit.getWorld(plugin.getIslandsWorldName());
    }

}
