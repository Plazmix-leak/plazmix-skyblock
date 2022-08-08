package net.plazmix.skyblock.oneblock.listener;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.skyblock.api.util.IslandUtil;
import net.plazmix.skyblock.api.util.SkyblockSpawnUtil;
import net.plazmix.skyblock.oneblock.event.IslandLevelUpEvent;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.cooldown.PlayerCooldownUtil;
import net.plazmix.utility.player.PlazmixUser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class SkyOneblockListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(SkyBlockApi.getInstance().getPlugin(), () -> {
            SkyIsland currentIsland = SkyBlockApi.getInstance().getIsland(player.getLocation());

            if (currentIsland != null) {
                currentIsland.createBorder(IslandUtil.getIslandBorder(currentIsland));

                currentPlayerIslands.put(player.getName().toLowerCase(), currentIsland);
            }

        }, 20);
    }

    @EventHandler
    public void onPlayerFall(PlayerMoveEvent event) {
        if (event.getTo().getY() < 0) {

            event.getPlayer().setGameMode(GameMode.SURVIVAL);
            event.getPlayer().damage(event.getPlayer().getHealthScale());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlazmixUser plazmixUser = PlazmixUser.of(player);

        event.setKeepInventory(!plazmixUser.getGroup().isDefault());
        event.setKeepLevel(event.getKeepInventory());

        player.spigot().respawn();

        if (SkyBlockApi.getInstance().hasIsland(player.getName())) {
            SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(player.getName());

            Location location = IslandUtil.getIslandLocation(skyIsland);
            if (location == null)
                return;

            player.teleport(SkyblockSpawnUtil.getSpawnLocation());
        }

        event.setDeathMessage(null);
    }


    private static final Material[] LOOTABLE_CHEST_STORAGE = {
            Material.WATER_BUCKET,
            Material.CACTUS,
            Material.SAND,
            Material.COAL,
            Material.IRON_INGOT,
            Material.INK_SACK,
            Material.WOOL,
            Material.WOOD,
            Material.SEEDS,
            Material.BEETROOT_SEEDS,
            Material.PUMPKIN_SEEDS,
            Material.CAKE,
            Material.EGG,
            Material.FLINT,
            Material.IRON_NUGGET,
            Material.GOLD_NUGGET,
            Material.SUGAR_CANE,
            Material.SUGAR,
            Material.STICK,
            Material.WOOD,
            Material.DEAD_BUSH,
    };

    @EventHandler
    public void onIslandLevelUp(IslandLevelUpEvent event) {
        Player player = event.getPlayer();
        PlazmixUser plazmixUser = PlazmixUser.of(player);

        // Sounds
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);

        int experience = NumberUtil.randomInt(10, 15) * event.getNewLevel();
        int coins = NumberUtil.randomInt(50, 100) * event.getNewLevel();

        // Announce
        player.sendTitle("§b§lУРОВЕНЬ ПОВЫШЕН", "Вы повысилы уровень острова с §c" + event.getOldLevel() + " §fдо §a" + event.getNewLevel());

        String message = "\n" +
                "§6§lSKYBLOCK ONEBLOCK §8:: §d§lУРОВЕНЬ ОСТРОВА ПОВЫШЕН!\n" +
                " §fВаш новый уровень: §a" + event.getNewLevel() + " LvL\n" +
                " §fДо следующего уровня необходимо сломать §e100 §fблоков\n" +
                "\n" +
                "§fВаша награда:\n" +
                " §e+" + coins + " монет\n" +
                " §3+" + experience + " EXP\n";

        player.spigot().sendMessage(new ComponentBuilder(message).create()); // Чтобы отступы считались, я сделал через BaseComponent

        // Give to player a level prize
        plazmixUser.addCoins(coins);
        plazmixUser.addExperience(experience);

        // Create lootable chest
        Location location = IslandUtil.getIslandLocation(event.getIsland());

        Block block = location.getBlock();
        block.setType(Material.CHEST);

        {
            // Fill lootable chest
            Chest chest = ((Chest) block.getState());
            Inventory inventory = chest.getBlockInventory();

            for (int i = 0; i < 15; i++) {
                Material material = Arrays.stream(LOOTABLE_CHEST_STORAGE).skip((long) (LOOTABLE_CHEST_STORAGE.length * Math.random())).findFirst().orElse(Material.AIR);

                int inventorySlot = NumberUtil.randomInt(0, 27);

                int durability = NumberUtil.randomInt(0, Math.max(1, material.getMaxDurability()));
                int amount = NumberUtil.randomInt(0, Math.min(16, material.getMaxStackSize())) + 1;

                ItemStack itemStack = new ItemStack(material, amount, (short) durability);
                inventory.setItem(inventorySlot, itemStack);
            }

            // chest.update();
        }
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
    }


    private final Map<String, SkyIsland> currentPlayerIslands = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Cooldown check
        if (event.isCancelled() || PlayerCooldownUtil.hasCooldown("move_island_check", player)) {
            return;
        }

        PlayerCooldownUtil.putCooldown("move_island_check", player, 1000);
        SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(event.getTo());

        if (skyIsland == null) {
            return;
        }

        // Announce
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("§eОстров " + skyIsland.getSkyPlayer().getHandle().getDisplayName()).create());

        // Move island WorldBorder
        SkyIsland oldIsland = currentPlayerIslands.get(player.getName().toLowerCase());
        currentPlayerIslands.put(player.getName().toLowerCase(), skyIsland);

        if (oldIsland != null && !oldIsland.equals(skyIsland)) {
            skyIsland.createBorder(IslandUtil.getIslandBorder(skyIsland));
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(event.getTo());

        if (skyIsland == null) {
            return;
        }

        // Announce
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("§eОстров " + skyIsland.getSkyPlayer().getHandle().getDisplayName()).create());

        // Move island WorldBorder
        SkyIsland oldIsland = currentPlayerIslands.get(player.getName().toLowerCase());
        currentPlayerIslands.put(player.getName().toLowerCase(), skyIsland);

        if (oldIsland != null && !oldIsland.equals(skyIsland)) {
            skyIsland.createBorder(IslandUtil.getIslandBorder(skyIsland));
        }
    }

}
