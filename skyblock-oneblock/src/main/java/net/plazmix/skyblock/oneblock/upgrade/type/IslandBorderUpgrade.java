package net.plazmix.skyblock.oneblock.upgrade.type;

import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.island.upgrade.IslandUpgrade;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.player.PlazmixUser;

public class IslandBorderUpgrade implements IslandUpgrade {

    public static final int UPGRADE_PRICE_CONF = 31_000;
    public static final int MAX_LEVEL = 10;

    @Override
    public ItemStack getItemStack(@NonNull Player player) {
        SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(player.getName());
        PlazmixUser plazmixUser = PlazmixUser.of(player);

        int upgradeLevel = skyIsland.getUpgradeLevel(this);
        int upgradeCost = upgradeLevel * UPGRADE_PRICE_CONF;

        ChatColor chatColor = (plazmixUser.getCoins() >= upgradeCost ? ChatColor.YELLOW : ChatColor.RED);


        ItemUtil.ItemBuilder itemBuilder = ItemUtil.newBuilder(Material.BEACON);
        itemBuilder.setName(chatColor + "Граница острова");

        itemBuilder.addLore("");
        itemBuilder.addLore("§7С каждым новым уровнем радиус границы");
        itemBuilder.addLore("§7Вашего острова увеличивается на §f30");
        itemBuilder.addLore("§fблоков§7, принося больше пространства");
        itemBuilder.addLore("§7для воплощения своих идей");

        itemBuilder.addLore("");
        itemBuilder.addLore("§7Текущий радиус: §c" + ((upgradeLevel * 30) + "x" + (upgradeLevel * 30)));
        itemBuilder.addLore("§7Текущий уровень: " + chatColor + NumberUtil.spaced(upgradeLevel));

        if (upgradeLevel < MAX_LEVEL) {
            itemBuilder.addLore("§7Цена улучшения: " + chatColor + NumberUtil.spaced(upgradeCost));
            itemBuilder.addLore("");

            if (plazmixUser.getCoins() >= upgradeCost) {
                itemBuilder.addLore(chatColor + "Нажмите, чтобы улучшить!");

            } else {

                itemBuilder.addLore(chatColor + "Недостаточно средств для улучшения!");
            }

        } else {

            itemBuilder.setGlowing(true);

            itemBuilder.addLore("");
            itemBuilder.addLore("§c§lМАКСИМАЛЬНЫЙ УРОВЕНЬ!");
        }

        return itemBuilder.build();
    }

    @Override
    public void onItemAction(@NonNull Player player, @NonNull SkyIsland skyIsland) {
        PlazmixUser plazmixUser = PlazmixUser.of(player);

        int upgradeLevel = skyIsland.getUpgradeLevel(this);
        int upgradeCost = upgradeLevel * UPGRADE_PRICE_CONF;


        if (upgradeLevel >= MAX_LEVEL) {
            plazmixUser.handle().sendMessage("§cВы достигли максимального уровня!");
            return;
        }

        if (plazmixUser.getCoins() < upgradeCost) {
            plazmixUser.handle().sendMessage("§cУ Вас недостаточно средств для данного улучшения!");
            return;
        }

        upgradeLevel++;

        skyIsland.alertMessage(true, "§6§lOneBlock §8:: §fГраница острова была улучшена до §e" + upgradeLevel + " уровня" + " §7(" + ((upgradeLevel * 30) + "x" + (upgradeLevel * 30)) + ")");
        plazmixUser.removeCoins(upgradeCost);

        skyIsland.createBorder(upgradeLevel * 30);
        skyIsland.upgradeLevel(this);
    }

}
