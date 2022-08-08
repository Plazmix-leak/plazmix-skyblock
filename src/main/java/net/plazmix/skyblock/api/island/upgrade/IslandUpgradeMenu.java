package net.plazmix.skyblock.api.island.upgrade;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import net.plazmix.inventory.impl.BasePaginatedInventory;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.island.IslandMenu;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.utility.ItemUtil;

public class IslandUpgradeMenu extends BasePaginatedInventory {

    @Getter
    @Setter
    private static IslandUpgradeManager upgradeManager;

    public IslandUpgradeMenu() {
        super("Улучшения острова", 6);
    }

    @Override
    public void drawInventory(Player player) {
        SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(player.getName());

        addRowToMarkup(2, 1);
        addRowToMarkup(3, 1);
        addRowToMarkup(4, 1);
        addRowToMarkup(5, 1);

        setClickItem(50, ItemUtil.newBuilder(Material.SKULL_ITEM)
                        .setDurability(3)
                        .setTextureValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzYyNTkwMmIzODllZDZjMTQ3NTc0ZTQyMmRhOGY4ZjM2MWM4ZWI1N2U3NjMxNjc2YTcyNzc3ZTdiMWQifX19")

                        .setName("§eВернуться назад")
                        .setLore("§7Нажмите, чтобы вернуться назад")
                        .build(),

                (player1, inventoryClickEvent) -> new IslandMenu().openInventory(player));


        if (upgradeManager == null) {
            return;
        }

        if (!upgradeManager.isInitialized())
            upgradeManager.initialize();

        for (IslandUpgrade islandUpgrade : upgradeManager.getIslandUpgrades()) {

            addClickItemToMarkup(islandUpgrade.getItemStack(player), (player1, inventoryClickEvent) -> {

                islandUpgrade.onItemAction(player1, skyIsland);
                updateInventory(player);
            });
        }
    }

}
