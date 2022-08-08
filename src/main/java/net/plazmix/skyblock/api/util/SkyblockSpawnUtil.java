package net.plazmix.skyblock.api.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import net.plazmix.event.PlayerDamageByPlayerEvent;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.automine.AutoMineManager;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.skyblock.api.pvp.BattleArenaManager;
import net.plazmix.utility.cooldown.PlayerCooldownUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.meta.FireworkMeta;

@UtilityClass
public class SkyblockSpawnUtil {

    private static Location spawnLocation = null;

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(@NonNull Location spawnLocation) {
        SkyblockSpawnUtil.spawnLocation = spawnLocation;
        spawnLocation.getWorld().setSpawnLocation((int)spawnLocation.getX(), (int)spawnLocation.getY(), (int)spawnLocation.getZ());

        Bukkit.getPluginManager().registerEvents(new SpawnListener(spawnLocation.getWorld()), SkyBlockApi.getInstance().getPlugin());
    }

    @RequiredArgsConstructor
    private final class SpawnListener implements Listener {

        private final World spawnWorld;

        private boolean hasGuard(@NonNull World world) {
            return world.equals(spawnWorld);
        }


        @EventHandler
        public void onEntityDeath(EntityDeathEvent event) {
            if (!hasGuard(event.getEntity().getWorld())) {
                return;
            }

            event.setDroppedExp(0);
            event.getDrops().clear();
        }

        @EventHandler
        public void onLeavesDecay(LeavesDecayEvent event) {
            if (!hasGuard(event.getBlock().getWorld())) {
                return;
            }

            event.setCancelled(true);
        }

        @EventHandler
        public void onWorldChanged(PlayerChangedWorldEvent event) {
            Player player = event.getPlayer();

            if (!hasGuard(event.getPlayer().getWorld())) {

                player.setAllowFlight(false);
                player.setFlying(false);
                return;
            }

            if (PlazmixUser.of(player).getGroup().isDefault()) {
                return;
            }

            player.setAllowFlight(true);
            player.setFlying(true);
        }

        @EventHandler
        public void onPortalJump(PlayerPortalEvent event) {
            Player player = event.getPlayer();

            if (!hasGuard(player.getWorld())) {
                return;
            }

            event.setCancelled(true);
            event.useTravelAgent(false);

            if (PlayerCooldownUtil.hasCooldown("island_tp_portal", player)) {
                return;
            }

            PlayerCooldownUtil.putCooldown("island_tp_portal", player, 1000);

            if (SkyBlockApi.getInstance().hasIsland(player.getName())) {

                SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(player.getName());
                Location islandLocation = IslandUtil.getIslandLocation(skyIsland);

                player.teleport(islandLocation.clone().add(0.5, 0, 0.5));

                skyIsland.createBorder(IslandUtil.getIslandBorder(skyIsland));

            } else {

                SkyBlockApi.getInstance().getPlugin().handleIslandCreation(player);
            }
        }

        @EventHandler
        public void onCreatureSpawn(CreatureSpawnEvent event) {
            if (!hasGuard(event.getEntity().getWorld())) {
                return;
            }

            if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
                return;
            }

            event.setCancelled(true);
        }

        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent event) {
            Block clickedBlock = event.getClickedBlock();

            if (!hasGuard(event.getPlayer().getWorld())) {
                return;
            }

            if (clickedBlock != null) {
                switch (clickedBlock.getType()) {

                    case CHEST:
                    case ENDER_CHEST:
                    case WORKBENCH:
                        return;
                }

                if (AutoMineManager.INSTANCE.getCuboidRegion() != null && AutoMineManager.INSTANCE.getCuboidRegion().contains(clickedBlock)) {
                    return;
                }
            }

            event.setCancelled(true);
        }

        @EventHandler
        public void onEntityDamage(EntityDamageEvent event) {
            if (!hasGuard(event.getEntity().getWorld())) {
                return;
            }

            if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && BattleArenaManager.INSTANCE.getCuboidRegion() != null && BattleArenaManager.INSTANCE.getCuboidRegion().contains(event.getEntity().getLocation())) {
                return;
            }

            event.setCancelled(true);
        }

        @EventHandler
        public void onBlockPhysics(BlockPhysicsEvent event) {
            if (!hasGuard(event.getBlock().getWorld())) {
                return;
            }

            event.setCancelled(true);
        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            PlazmixUser plazmixUser = PlazmixUser.of(player);

            player.setFlySpeed(0.1f);
            player.setWalkSpeed(0.2f);

            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(spawnLocation);

            if (!plazmixUser.getGroup().isDefault()) {

                player.setAllowFlight(true);
                player.setFlying(true);

                Firework firework = spawnWorld.spawn(player.getLocation(), Firework.class);
                FireworkMeta fireworkMeta = firework.getFireworkMeta();

                fireworkMeta.setPower(3);
                fireworkMeta.addEffect(FireworkEffect.builder()
                        .with(FireworkEffect.Type.STAR)

                        .withColor(Color.PURPLE)
                        .withColor(Color.WHITE)

                        .build());

                firework.setFireworkMeta(fireworkMeta);
            }
        }

        @EventHandler
        public void onBreak(BlockBreakEvent event) {
            if (!hasGuard(event.getBlock().getWorld())) {
                return;
            }

            if (AutoMineManager.INSTANCE.getCuboidRegion() != null && AutoMineManager.INSTANCE.getCuboidRegion().contains(event.getBlock())) {
                return;
            }

            event.setCancelled(true);
        }

        @EventHandler
        public void onPlace(BlockPlaceEvent event) {
            if (!hasGuard(event.getBlock().getWorld())) {
                return;
            }

            event.setCancelled(true);
        }

        @EventHandler
        public void onPlayerMove(PlayerMoveEvent event) {
            Location playerLocation = event.getPlayer().getLocation();

            if (!hasGuard(playerLocation.getWorld())) {
                return;
            }

            if (BattleArenaManager.INSTANCE.getCuboidRegion() != null) {
                if (BattleArenaManager.INSTANCE.getCuboidRegion().contains(playerLocation)) {
                    event.getPlayer().setAllowFlight(false);

                } else {

                    event.getPlayer().setAllowFlight(!PlazmixUser.of(event.getPlayer()).getGroup().isDefault());
                }
            }

            if (event.getTo().getY() <= 0) {
                event.getPlayer().teleport(spawnLocation);
            }
        }

        @EventHandler
        public void onPlayerDamageByPlayer(PlayerDamageByPlayerEvent event) {
            if (!hasGuard(event.getDamager().getWorld())) {
                return;
            }

            if (BattleArenaManager.INSTANCE.getCuboidRegion() != null && BattleArenaManager.INSTANCE.getCuboidRegion().contains(event.getDamager().getLocation())) {
                event.setCancelled(false);
                return;
            }

            event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onBucketEmpty(PlayerBucketEmptyEvent event) {
            if (!hasGuard(event.getPlayer().getWorld())) {
                return;
            }

            event.setCancelled(true);
        }
    }

}
