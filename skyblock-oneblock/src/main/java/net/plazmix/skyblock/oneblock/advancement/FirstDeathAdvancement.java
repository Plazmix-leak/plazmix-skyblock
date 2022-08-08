package net.plazmix.skyblock.oneblock.advancement;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import net.plazmix.skyblock.api.advancement.SkyAdvancement;

public class FirstDeathAdvancement extends SkyAdvancement {

    public FirstDeathAdvancement() {
        super("tutorial_end", Material.BARRIER, "first_death",
                "§cПервая смерть",
                "Плохой опыт - тоже опыт!");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        advance(event.getEntity().getName());
    }

}
