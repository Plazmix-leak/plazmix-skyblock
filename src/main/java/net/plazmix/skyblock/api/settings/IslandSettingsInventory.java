package net.plazmix.skyblock.api.settings;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import net.plazmix.inventory.BaseInventory;
import net.plazmix.inventory.impl.BasePaginatedInventory;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.utility.ItemUtil;

public class IslandSettingsInventory extends BasePaginatedInventory {

    private final BaseInventory backwardInventory;

    public IslandSettingsInventory(BaseInventory backwardInventory) {
        super("Настройка острова", 5);

        this.backwardInventory = backwardInventory;
    }

    @Override
    public void drawInventory(Player player) {
        SkyIsland island = SkyBlockApi.getInstance().getIsland(player.getName());

        addRowToMarkup(2, 1);
        addRowToMarkup(3, 1);
        addRowToMarkup(4, 1);

        // TODO: Не отрисовывает предметы настроек, а стрелочке "назад" заебись
        for (IslandSettings settings : IslandSettings.values()) {
            boolean activated = settings.get(island);

            ItemUtil.ItemBuilder itemBuilder = ItemUtil.newBuilder(settings.getIconType())
                    .setName((activated ? ChatColor.YELLOW : ChatColor.RED) + settings.getIconName());

            itemBuilder.addLore("§7Разрешение: " + (activated ? "§aесть" : "§cнет"));

            if (!settings.canUpdate(player)) {
                itemBuilder.addLore("");
                itemBuilder.addLore("§cДоступно от " + settings.getMinGroup().getPrefix() + " §cи выше!");

            } else {

                itemBuilder.addLore("");
                itemBuilder.addLore("§e▸ Нажмите, чтобы изменить");
            }

            itemBuilder.setGlowing(activated);
            addClickItemToMarkup(itemBuilder.build(), (player1, inventoryClickEvent) -> {

                settings.update(island, !activated);
                updateInventory(player);
            });
        }

        if (backwardInventory != null) {
            setClickItem(41, ItemUtil.newBuilder(Material.SKULL_ITEM)
                            .setDurability(3)
                            .setTextureValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzYyNTkwMmIzODllZDZjMTQ3NTc0ZTQyMmRhOGY4ZjM2MWM4ZWI1N2U3NjMxNjc2YTcyNzc3ZTdiMWQifX19")

                            .setName("§eВернуться назад")
                            .setLore("§7Нажмите, чтобы вернуться назад")
                            .build(),

                    (player1, inventoryClickEvent) -> backwardInventory.openInventory(player));
        }
    }

}
