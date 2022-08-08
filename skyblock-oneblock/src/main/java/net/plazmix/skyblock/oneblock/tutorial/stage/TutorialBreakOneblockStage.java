package net.plazmix.skyblock.oneblock.tutorial.stage;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.NonNull;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.skyblock.api.player.SkyPlayer;
import net.plazmix.skyblock.oneblock.tutorial.SkyOneblockTutorialStage;
import net.plazmix.protocollib.entity.animation.FakeEntityAnimation;
import net.plazmix.protocollib.entity.impl.FakePlayer;
import net.plazmix.skyblock.api.util.IslandUtil;

import java.util.stream.Stream;

public class TutorialBreakOneblockStage extends SkyOneblockTutorialStage {

    private FakePlayer fakePlayer;

    @Override
    protected void onEnable(@NonNull SkyPlayer skyPlayer) {
        SkyIsland island = skyPlayer.getIsland();
        Location center = IslandUtil.getIslandLocation(island);

        // Teleport player
        skyPlayer.getBukkitHandle().teleport(center.clone().add(4, 4, 4));

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
        addCommand(() -> {
            skyPlayer.getBukkitHandle().setAllowFlight(true);
            skyPlayer.getBukkitHandle().setFlying(true);
        });

        addCommand(() -> {

            Block block = center.getBlock();
            block.breakNaturally();

            block.setType(Stream.of(Material.STONE, Material.GRASS, Material.DIRT, Material.DIAMOND_ORE, Material.LOG)
                    .skip((long) (5 * Math.random()))
                    .findFirst()
                    .orElse(Material.GRASS));

            skyPlayer.getBukkitHandle().spigot()
                    .playEffect(block.getLocation().clone().add(0.5, 0.5, 0.5), Effect.CLOUD, 0, 0, 0.05F, 0.05F, 0.05F, 0.05F, 5, 50);
        });

        addCommand(() -> fakePlayer.playAnimationAll(FakeEntityAnimation.SWING_MAIN_HAND));
        addCommand(() -> skyPlayer.getBukkitHandle().sendTitle("§6§lДобыча ресурсов", "§fНа Вашем плоту раположен лишь один §cбесконечный §fблок", 0, 100, 0));
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
    }
}
