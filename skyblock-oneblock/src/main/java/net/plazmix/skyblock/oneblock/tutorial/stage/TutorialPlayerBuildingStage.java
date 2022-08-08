package net.plazmix.skyblock.oneblock.tutorial.stage;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import net.plazmix.schematic.BaseSchematic;
import net.plazmix.schematic.BaseSchematicBlock;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.skyblock.api.player.SkyPlayer;
import net.plazmix.skyblock.oneblock.tutorial.SkyOneblockTutorialStage;
import net.plazmix.skyblock.api.util.IslandUtil;
import net.plazmix.utility.location.region.CuboidRegion;

import java.io.IOException;
import java.util.*;

public class TutorialPlayerBuildingStage extends SkyOneblockTutorialStage {

    private BukkitRunnable bukkitRunnable = null;

    private final BaseSchematic schematic = new BaseSchematic("tutorial_oneblock_island");
    private final Collection<Location> locationCollection = new ArrayList<>();

    @Override
    protected void onEnable(@NonNull SkyPlayer skyPlayer) {
        SkyIsland island = skyPlayer.getIsland();
        Location center = IslandUtil.getIslandLocation(island);

        // Teleport player
        skyPlayer.getBukkitHandle().teleport(center.clone().add(7, 7, 7));

        Vector direction = center.clone().subtract(skyPlayer.getBukkitHandle().getLocation()).toVector().normalize();
        Location playerLocation = skyPlayer.getBukkitHandle().getLocation();

        playerLocation.setDirection(direction);
        skyPlayer.getBukkitHandle().teleport(playerLocation);

        // Add Commands
        (bukkitRunnable = new BukkitRunnable() {

            private Collection<BaseSchematicBlock> schematicBlocks;
            private Iterator<BaseSchematicBlock> schematicBlockIterator;

            private boolean sorted = false;

            {
                try {
                    schematicBlocks = schematic.reader().read();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void run() {
                if (!sorted) {
                    schematicBlockIterator = schematicBlocks.stream()
                            .sorted(Comparator.comparingDouble(value -> value.getBlock(center.clone().add(0, 1, 0)).getLocation().distance(center.clone().add(0, 1, 0))))
                            .iterator();

                    sorted = true;
                }

                BaseSchematicBlock schematicBlock = schematicBlockIterator.next();
                Location location = schematicBlock.getBlock(center.clone().add(0, 1, 0)).getLocation();

                locationCollection.add(location);

                skyPlayer.getBukkitHandle().sendBlockChange(location, schematicBlock.getMaterialData().getItemType(), schematicBlock.getMaterialData().getData());
            }

        }).runTaskTimer(SkyBlockApi.getInstance().getPlugin(), 0, 0);

        addCommand(() -> skyPlayer.getBukkitHandle().sendTitle("§6§lСтроительство", "§fСоздавайте себе удобную площадь для выживания §aсами§f!", 0, 100, 0));
    }

    @Override
    protected void onDisable(@NonNull SkyPlayer skyPlayer) {
        if (bukkitRunnable != null) {
            bukkitRunnable.cancel();

            bukkitRunnable = null;
        }

        for (Location location : locationCollection) {
            skyPlayer.getBukkitHandle().sendBlockChange(location, Material.AIR, (byte)0);
        }

        IslandUtil.getIslandLocation( skyPlayer.getIsland() ).getBlock()
                .setType(Material.GRASS);
    }
}
