package net.plazmix.skyblock.oneblock.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.util.IslandUtil;
import net.plazmix.skyblock.oneblock.SkyOneblockManager;

public final class TutorialListener implements Listener {

    @EventHandler
    public void onPlayerJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (SkyOneblockManager.INSTANCE.isInTutorial(player)) {
            if (event.getFrom().getY() == event.getTo().getY())
                return;

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (SkyOneblockManager.INSTANCE.isInTutorial(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (SkyOneblockManager.INSTANCE.isInTutorial(player) && SkyBlockApi.getInstance().hasIsland(player.getName())) {
            SkyOneblockManager.INSTANCE.getPlayerTutorialMap().remove(player.getName().toLowerCase());

            IslandUtil.deleteIsland( SkyBlockApi.getInstance().getIsland(player.getName()) );
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (SkyOneblockManager.INSTANCE.isInTutorial(player)) {
            event.setCancelled(true);
        }
    }

}
