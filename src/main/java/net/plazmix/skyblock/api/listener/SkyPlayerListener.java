package net.plazmix.skyblock.api.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.event.PlayerFirstJoinEvent;
import net.plazmix.skyblock.api.player.SkyPlayer;

import java.nio.file.Files;

public final class SkyPlayerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SkyPlayer skyPlayer = SkyBlockApi.getInstance().getPlayer(player);

        player.setMaxHealth(20);

        skyPlayer.inject();

        // Check player FIRST JOIN
        if (!SkyBlockApi.getInstance().hasIsland(skyPlayer)) {
            Bukkit.getPluginManager().callEvent(new PlayerFirstJoinEvent(player));

        } else {

            player.setWalkSpeed(0.2f);
            player.setFlySpeed(0.1f);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SkyPlayer skyPlayer = SkyBlockApi.getInstance().getPlayer(player);

        if (skyPlayer.getIsland() != null && Files.exists(skyPlayer.getIsland().getConfigurationPath()))
            skyPlayer.save();
    }
}
