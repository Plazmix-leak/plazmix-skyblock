package net.plazmix.skyblock.oneblock.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.skyblock.api.settings.IslandSettings;
import net.plazmix.skyblock.api.util.IslandUtil;
import net.plazmix.skyblock.api.util.SkyblockSpawnUtil;
import net.plazmix.skyblock.oneblock.SkyOneblockManager;

public final class IslandSettingsListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (SkyblockSpawnUtil.getSpawnLocation().getWorld().equals(block.getWorld())) {
            return;
        }

        SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(block.getLocation());
        if (skyIsland != null && IslandUtil.getIslandLocation(skyIsland) != null) {

            // Если создатель или участник острова что-то на нем ломает, то все ок
            if (skyIsland.isMember(player.getName()) || skyIsland.getSkyPlayer().getName().equalsIgnoreCase(player.getName())) {

                // Если был сломан основной блок острова
                if (IslandUtil.getIslandLocation(skyIsland).getBlock().getLocation().equals(block.getLocation())) {

                    // Drop velocity fix
                    event.setDropItems(false);

                    for (ItemStack drop : event.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
                        player.getWorld().dropItemNaturally(IslandUtil.getIslandLocation(skyIsland).clone().add(0.5, 1, 0.5), drop);
                    }

                    // block break
                    SkyOneblockManager.INSTANCE.breakIslandBlock(player, skyIsland);
                    event.setCancelled(true);
                }

                return;
            }

            // А если кто-то чужой, то пошел нахуй отсюда (мб)
            if (event.getBlock().getLocation().equals(IslandUtil.getIslandLocation(skyIsland))) {
                player.sendMessage("§cОшибка, Вы не можете ломать данный блок на острове " + skyIsland.getSkyPlayer().getName());

                event.setCancelled(true);
                return;
            }

            event.setCancelled(!IslandSettings.BUILD.get(skyIsland));
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();

        SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(event.getBlockClicked().getLocation());
        if (skyIsland != null && IslandUtil.getIslandLocation(skyIsland) != null) {

            // Если создатель или участник острова что-то на нем ломает, то все ок
            if (skyIsland.isMember(player.getName()) || skyIsland.getSkyPlayer().getName().equalsIgnoreCase(player.getName())) {
                return;
            }

            // А если кто-то чужой, то пошел нахуй отсюда (мб)
            event.setCancelled(!IslandSettings.USE.get(skyIsland));
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(event.getBlock().getLocation());
        if (skyIsland != null && IslandUtil.getIslandLocation(skyIsland) != null) {

            // Если создатель или участник острова что-то на нем ломает, то все ок
            if (skyIsland.isMember(player.getName()) || skyIsland.getSkyPlayer().getName().equalsIgnoreCase(player.getName())) {
                return;
            }

            // А если кто-то чужой, то пошел нахуй отсюда (мб)
            event.setCancelled(!IslandSettings.BUILD.get(skyIsland));
        }
    }

    @EventHandler
    public void onAnimalSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(event.getLocation());

        if (skyIsland == null) {
            return;
        }

        if (entity instanceof Animals) {
            event.setCancelled(!IslandSettings.ANIMAL_SPAWN.get(skyIsland));
        }
    }

    @EventHandler
    public void onMonsterSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(event.getLocation());

        if (skyIsland == null) {
            return;
        }

        if (entity instanceof Monster) {
            event.setCancelled(!IslandSettings.MONSTER_SPAWN.get(skyIsland));
        }
    }

    @EventHandler
    public void onPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();

        if (SkyblockSpawnUtil.getSpawnLocation().getWorld().equals(block.getWorld())) {
            return;
        }

        SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(block.getLocation());

        if (skyIsland == null)
            return;

        event.setCancelled(!IslandSettings.PHYSICS.get(skyIsland));
    }

    @EventHandler
    public void onPhysics(BlockFromToEvent event) {
        Block block = event.getBlock();

        if (SkyblockSpawnUtil.getSpawnLocation().getWorld().equals(block.getWorld())) {
            return;
        }

        SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(block.getLocation());

        if (skyIsland == null)
            return;

        event.setCancelled(!IslandSettings.PHYSICS.get(skyIsland));
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (SkyblockSpawnUtil.getSpawnLocation().getWorld().equals(player.getWorld())) {
            return;
        }

        SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(player.getLocation());

        if (skyIsland == null)
            return;

        if (!skyIsland.isMember(player.getName()) && !skyIsland.getSkyPlayer().getName().equalsIgnoreCase(player.getName())) {
            event.setCancelled(!IslandSettings.USE.get(skyIsland));
        }
    }

    @EventHandler
    public void onPVP(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if (SkyblockSpawnUtil.getSpawnLocation().getWorld().equals(damager.getWorld())) {
            return;
        }

        if (entity instanceof Player && damager instanceof Player) {
            SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(entity.getLocation());

            if (skyIsland == null)
                return;

            event.setCancelled(!IslandSettings.BATTLE.get(skyIsland));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity.getWorld().getName().equalsIgnoreCase("Spawn")) {
            return;
        }

        if (entity.getLocation().getY() <= 0) {
            return;
        }

        if (entity instanceof Player) {
            SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(entity.getLocation());

            if (skyIsland == null)
                return;

            event.setCancelled(!IslandSettings.DAMAGE.get(skyIsland));
        }
    }

}
