package net.plazmix.skyblock.api.island.upgrade;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.plazmix.skyblock.api.island.SkyIsland;

public interface IslandUpgrade {

    ItemStack getItemStack(@NonNull Player player);

    void onItemAction(@NonNull Player player, @NonNull SkyIsland skyIsland);
}
