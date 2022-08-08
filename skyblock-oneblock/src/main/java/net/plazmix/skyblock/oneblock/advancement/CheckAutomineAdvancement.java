package net.plazmix.skyblock.oneblock.advancement;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import net.plazmix.skyblock.api.advancement.SkyAdvancement;
import net.plazmix.skyblock.api.automine.AutoMineManager;

public class CheckAutomineAdvancement extends SkyAdvancement {

    public CheckAutomineAdvancement() {
        super(Material.IRON_PICKAXE, "check_automine",
                "§eПосетить автоматическую шахту",
                "Здесь игроки получают ресурсы, а также эта местность обновляется каждые §c5 минут");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (AutoMineManager.INSTANCE.getCuboidRegion() != null && AutoMineManager.INSTANCE.getCuboidRegion().contains(player.getLocation())) {
            advance(player.getName());
        }
    }

}
