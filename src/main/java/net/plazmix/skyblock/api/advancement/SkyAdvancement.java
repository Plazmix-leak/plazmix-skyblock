package net.plazmix.skyblock.api.advancement;

import lombok.NonNull;
import net.plazmix.advancement.PlazmixAdvancement;
import net.plazmix.advancement.PlazmixAdvancementMenu;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import net.plazmix.skyblock.api.SkyBlockApi;

public class SkyAdvancement
        extends PlazmixAdvancement implements Listener {

    protected static final SkyAdvancementManager ADVANCEMENT_MANAGER = SkyBlockApi.getInstance().getAdvancementManager();
    protected static final PlazmixAdvancementMenu ADVANCEMENT_MENU = ADVANCEMENT_MANAGER.getAdvancementMenu();


    public SkyAdvancement(@NonNull String parentAdvancementName, @NonNull String minecraftIconMaterial, @NonNull String advancementKeyName, @NonNull String advancementTitle, @NonNull String advancementDescription) {
        super(ADVANCEMENT_MENU, ADVANCEMENT_MANAGER.getAdvancement(parentAdvancementName).getNamespaceKey(), minecraftIconMaterial, advancementKeyName, advancementTitle, advancementDescription);
    }

    public SkyAdvancement(@NonNull String parentAdvancementName, @NonNull Material material, @NonNull String advancementKeyName, @NonNull String advancementTitle, @NonNull String advancementDescription) {
        super(ADVANCEMENT_MENU, ADVANCEMENT_MANAGER.getAdvancement(parentAdvancementName).getNamespaceKey(), material.name().toLowerCase(), advancementKeyName, advancementTitle, advancementDescription);
    }


    public SkyAdvancement(@NonNull String minecraftIconMaterial, @NonNull String advancementKeyName, @NonNull String advancementTitle, @NonNull String advancementDescription) {
        super(ADVANCEMENT_MENU, ADVANCEMENT_MENU.getNamespaceKey(), minecraftIconMaterial, advancementKeyName, advancementTitle, advancementDescription);
    }

    public SkyAdvancement(@NonNull Material material, @NonNull String advancementKeyName, @NonNull String advancementTitle, @NonNull String advancementDescription) {
        super(ADVANCEMENT_MENU, ADVANCEMENT_MENU.getNamespaceKey(), material.name().toLowerCase(), advancementKeyName, advancementTitle, advancementDescription);
    }

}
