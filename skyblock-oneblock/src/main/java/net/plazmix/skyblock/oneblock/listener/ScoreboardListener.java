package net.plazmix.skyblock.oneblock.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.oneblock.event.TutorialEndEvent;
import net.plazmix.skyblock.oneblock.scoreboard.OneblockScoreboard;

public final class ScoreboardListener implements Listener {

    @EventHandler
    public void onTutorialEnd(TutorialEndEvent event) {
        new OneblockScoreboard(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(SkyBlockApi.getInstance().getPlugin(), () -> new OneblockScoreboard(event.getPlayer()), 10);
    }

}
