package net.plazmix.skyblock.api.advancement;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import net.plazmix.advancement.PlazmixAdvancementMenu;
import net.plazmix.skyblock.api.SkyBlockApi;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class SkyAdvancementManager {

    private final PlazmixAdvancementMenu advancementMenu = new PlazmixAdvancementMenu(Material.CAULDRON, "skyblock",
            "§eВыживание среди пустоты",
            "§7Выживание на острове, посреди неизведанной пустоты",
            "textures/colormap/foliage.png");

    private final Map<String, SkyAdvancement> advancementMap = new HashMap<>();


    public void registerAdvancement(@NonNull String advancementName, @NonNull SkyAdvancement skyAdvancement) {
        advancementMap.put(advancementName.toLowerCase(), skyAdvancement);

        Bukkit.getPluginManager().registerEvents(skyAdvancement, SkyBlockApi.getInstance().getPlugin());
    }

    public void registerAdvancement(@NonNull SkyAdvancement skyAdvancement) {
        registerAdvancement(skyAdvancement.getAdvancementKeyName(), skyAdvancement);
    }

    public SkyAdvancement getAdvancement(@NonNull String advancementName) {
        return advancementMap.get(advancementName.toLowerCase());
    }

    public void advance(@NonNull Player player, @NonNull String advancementName) {
        SkyAdvancement skyAdvancement = getAdvancement(advancementName);

        if (skyAdvancement != null) {
            skyAdvancement.advance(player.getName());
        }
    }

}
