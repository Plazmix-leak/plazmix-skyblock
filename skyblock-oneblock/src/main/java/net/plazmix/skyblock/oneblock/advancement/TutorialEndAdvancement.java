package net.plazmix.skyblock.oneblock.advancement;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import net.plazmix.skyblock.api.advancement.SkyAdvancement;
import net.plazmix.skyblock.oneblock.event.TutorialEndEvent;

public class TutorialEndAdvancement extends SkyAdvancement {

    public TutorialEndAdvancement() {
        super(Material.FISHING_ROD, "tutorial_end",
                "§bПрохождение туториала",
                "Перед началом игры пройти полное обучение по режиму");
    }

    @EventHandler
    public void onTutorialEnd(TutorialEndEvent event) {
        advance(event.getPlayer().getName());
    }

}
