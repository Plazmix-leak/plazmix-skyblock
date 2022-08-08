package net.plazmix.skyblock.oneblock.advancement;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import net.plazmix.skyblock.api.advancement.SkyAdvancement;
import net.plazmix.skyblock.oneblock.event.IslandLevelUpEvent;

public class IslandLevelupAdvancement extends SkyAdvancement {

    public IslandLevelupAdvancement() {
        super("tutorial_end", Material.EMERALD, "island_levelup",
                "§dНовые ресуры!",
                "Вы полностью освоили азы режима, теперь все зависит только от Вас!");
    }

    @EventHandler
    public void onIslandLevelup(IslandLevelUpEvent event) {
        advance(event.getPlayer().getName());
    }

}
