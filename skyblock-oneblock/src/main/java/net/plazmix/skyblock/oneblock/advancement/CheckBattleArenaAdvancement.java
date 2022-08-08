package net.plazmix.skyblock.oneblock.advancement;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import net.plazmix.skyblock.api.advancement.SkyAdvancement;
import net.plazmix.skyblock.api.pvp.BattleArenaManager;

public class CheckBattleArenaAdvancement extends SkyAdvancement {

    public CheckBattleArenaAdvancement() {
        super(Material.IRON_SWORD, "check_battle_arena",
                "§eПосетить боевую арену",
                "Здесь игроки испытывают себя, как войны");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (BattleArenaManager.INSTANCE.getCuboidRegion() != null && BattleArenaManager.INSTANCE.getCuboidRegion().contains(player.getLocation())) {
            advance(player.getName());
        }
    }

}
