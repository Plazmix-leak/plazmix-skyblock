package net.plazmix.skyblock.oneblock.tutorial.stage;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import net.plazmix.protocollib.entity.animation.FakeEntityAnimation;
import net.plazmix.protocollib.entity.impl.FakePlayer;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.skyblock.api.player.SkyPlayer;
import net.plazmix.skyblock.api.util.IslandUtil;
import net.plazmix.skyblock.oneblock.tutorial.SkyOneblockTutorialStage;

import java.util.Arrays;
import java.util.ListIterator;

public class TutorialIslandLevelUpStage extends SkyOneblockTutorialStage {

    private FakePlayer fakePlayer;

    private final ListIterator<Material> listIterator
            = Arrays.asList(Material.DIRT, Material.LOG, Material.STONE, Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.LAPIS_ORE, Material.REDSTONE_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE)
            .listIterator();

    @Override
    protected void onEnable(@NonNull SkyPlayer skyPlayer) {
        SkyIsland island = skyPlayer.getIsland();
        Location center = IslandUtil.getIslandLocation(island);


        // Teleport player
        skyPlayer.getBukkitHandle().teleport(center.clone().add(3, 1, 0));

        // Looking player
        Vector direction = center.clone().subtract(skyPlayer.getBukkitHandle().getLocation()).toVector().normalize();
        Location playerLocation = skyPlayer.getBukkitHandle().getLocation();

        playerLocation.setDirection(direction);
        skyPlayer.getBukkitHandle().teleport(playerLocation);


        // Create NPC
        fakePlayer = new FakePlayer(skyPlayer.getName(), center.clone().add(0.5, 1, 0.5));

        fakePlayer.getEntityEquipment()
                .setEquipment(EnumWrappers.ItemSlot.MAINHAND, new ItemStack(Material.LOG));

        fakePlayer.look(0, 90);
        fakePlayer.addViewers(skyPlayer.getBukkitHandle());


        // Add Commands
        addCommand(() -> fakePlayer.playAnimationAll(FakeEntityAnimation.SWING_MAIN_HAND));
        addCommand(() -> {
            center.getBlock().breakNaturally();

            center.getBlock().setType(listIterator.next());
            center.getWorld().spigot().playEffect(center.clone().add(0.5, 0.5, 0.5), Effect.CLOUD, 0, 0, 0.05F, 0.05F, 0.05F, 0.05F, 5, 50);
        });

        addCommand(() -> skyPlayer.getBukkitHandle().sendTitle("§6§lПрокачка уровня", "§fС каждым сломанным §a100-ым §fблоком открываются новые материалы", 0, 100, 0));
    }

    @Override
    protected void onDisable(@NonNull SkyPlayer skyPlayer) {
        fakePlayer.removeViewers(skyPlayer.getBukkitHandle());
        fakePlayer = null;

        Location center = IslandUtil.getIslandLocation(skyPlayer.getIsland());
        center.getBlock().setType(Material.GRASS);

        for (Entity entity : SkyBlockApi.getInstance().getIslandWorld().getNearbyEntities(center, 5, 5, 5))
            if (!(entity instanceof Player))
                entity.remove();



        // START PLAYING
        skyPlayer.getBukkitHandle().playSound(skyPlayer.getBukkitHandle().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        skyPlayer.getBukkitHandle().setFlySpeed(0.1f);
        skyPlayer.getBukkitHandle().setWalkSpeed(0.2f);

        skyPlayer.getBukkitHandle().setAllowFlight(false);
        skyPlayer.getBukkitHandle().setFlying(false);

        skyPlayer.getBukkitHandle().setGameMode(GameMode.SURVIVAL);

        // Teleport player
        skyPlayer.getBukkitHandle().teleport(center.clone().add(0.5, 1, 0.5));

        Vector direction = center.clone().subtract(skyPlayer.getBukkitHandle().getLocation()).toVector().normalize();
        Location playerLocation = skyPlayer.getBukkitHandle().getLocation();

        playerLocation.setDirection(direction);
        skyPlayer.getBukkitHandle().teleport(playerLocation);

        // Title announce
        skyPlayer.getBukkitHandle().sendTitle("§e§lOneBlock", "§fНачните свое §cвыживание §fпрямо сейчас!");
    }
}
