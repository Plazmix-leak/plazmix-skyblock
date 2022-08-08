package net.plazmix.skyblock.api.island;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.plazmix.inventory.impl.BaseSimpleInventory;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.island.upgrade.IslandUpgradeMenu;
import net.plazmix.skyblock.api.settings.IslandSettingsInventory;
import net.plazmix.skyblock.api.util.IslandUtil;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.NumberUtil;

public class IslandMenu extends BaseSimpleInventory {

    public IslandMenu() {
        super("Меню острова", 6);
    }

    @Override
    public void drawInventory(Player player) {
        SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(player.getName());

        setClickItem(5, ItemUtil.newBuilder(Material.EMERALD)
                .setName("§a§lМагазин ресурсов")
                .addLore("§7§oНажмите, чтобы перейти в магазин ресурсов!")
                .build(),

                (player1, inventoryClickEvent) -> Bukkit.dispatchCommand(player, "shop"));

        setOriginalItem(14, ItemUtil.newBuilder(Material.SKULL_ITEM)
                .setDurability(3)
                .setName(ChatColor.YELLOW + "Остров " + skyIsland.getSkyPlayer().getHandle().getDisplayName())
                .addLore("")
                .addLore("§8Статистика:")
                .addLore(" §7Текущий уровень: §b" + NumberUtil.spaced(skyIsland.getIslandData("blocks", 0) / 100))
                .addLore(" §7До следующего уровня: §e" + (skyIsland.getIslandData("blocks", 0) % 100) + "§7/§a100 §7блоков")
                .addLore(" §7Количество участников: §e" + NumberUtil.spaced(skyIsland.getMembersCount()))
                .addLore("")

                .addLore("§8Дополнительно:")
                .addLore(" §7Ограничение: §e300x300") //TODO
                .addLore(" §7Всего сломано: §e" + NumberUtil.spaced(skyIsland.getIslandData("blocks", 0)))
                .addLore("")

                .setPlayerSkull(player.getName())
                .build());

        setOriginalItem(30, ItemUtil.newBuilder(Material.EXP_BOTTLE)
                .setName("§eУровень острова")
                .addLore("")
                .addLore("§7За каждые сломанные §c100 §7бесконечного")
                .addLore("§7OneBlock'а на острове, Вы повышаете уровень,")
                .addLore("§7получая взамен спавн новых материалов, а также")
                .addLore("§7презент в виде §6коинов")
                .addLore("")
                .addLore("§fТекущий уровень: §b" + skyIsland.getIslandData("blocks", 0) / 100)
                .addLore("§fДо следующего уровня: §e" + (skyIsland.getIslandData("blocks", 0) % 100) + "§7/§a100 §fблоков")
                .build());

        setClickItem(32, ItemUtil.newBuilder(Material.BED)
                .setDurability(9)

                .setName("§eТелепортация на остров")
                .addLore("§7Нажмите, чтобы переместиться на остров!")
                .build(),

                (player1, inventoryClickEvent) -> {

                    player.teleport(IslandUtil.getIslandLocation(skyIsland).clone().add(0.5, 1, 0.5));
                    skyIsland.createBorder(IslandUtil.getIslandBorder(skyIsland));
                });

        setClickItem(34, ItemUtil.newBuilder(Material.REDSTONE_COMPARATOR)
                        .setName("§eНастройки")
                        .addLore("§7Нажмите, чтобы открыть настройки!")
                        .build(),

                (player1, inventoryClickEvent) -> new IslandSettingsInventory(this).openInventory(player));



        setClickItem(49, ItemUtil.newBuilder(Material.EYE_OF_ENDER)
                .setName(ChatColor.YELLOW + "Улучшения острова")
                .addLore("§7Нажмите, чтобы открыть улучшения острова!")

                .build(),

                (player1, inventoryClickEvent) -> new IslandUpgradeMenu().openInventory(player));

        setOriginalItem(50, ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                .setDurability(14)
                .setName(ChatColor.RED + "Скоро тут что-то будет...")
                .build());

        setOriginalItem(51, ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                .setDurability(14)
                .setName(ChatColor.RED + "Скоро тут что-то будет...")
                .build());


        // Inventory Frames.
        ItemStack frameItem = ItemUtil.newBuilder(Material.STAINED_GLASS_PANE).setDurability(3).setName(ChatColor.RESET.toString()).build();

        setOriginalItem(1, frameItem);
        setOriginalItem(2, frameItem);
        setOriginalItem(8, frameItem);
        setOriginalItem(9, frameItem);
        setOriginalItem(10, frameItem);
        setOriginalItem(18, frameItem);

        setOriginalItem(37, frameItem);
        setOriginalItem(45, frameItem);
        setOriginalItem(46, frameItem);
        setOriginalItem(47, frameItem);
        setOriginalItem(53, frameItem);
        setOriginalItem(54, frameItem);
    }

}
