package net.plazmix.skyblock.api.util;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.utility.location.region.CuboidRegion;
import net.plazmix.utility.location.LocationUtil;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class IslandUtil {

    public SkyIsland getLastCreatedIsland() {
        File[] islandFiles = SkyBlockApi.getInstance().getPlugin().getDataFolder().listFiles();

        if (islandFiles == null || islandFiles.length == 0)
            return null;

        List<SkyIsland> availableIslands = Arrays.stream(islandFiles)

                .map(file -> file.getName().substring(0, file.getName().length() - 4))
                .map(name -> SkyBlockApi.getInstance().getIsland(name))

                .filter(island -> island != null && IslandUtil.getIslandLocation(island) != null)
                .collect(Collectors.toList());

        if (availableIslands.isEmpty()) {
            return null;
        }

        return availableIslands.get(availableIslands.size() - 1);
    }

    public Location newIslandLocation(int distance) {

        SkyIsland lastIsland = getLastCreatedIsland();
        if (lastIsland == null) {
            return new Location(SkyBlockApi.getInstance().getIslandWorld(), 0, 100, 0);
        }

        Location location = lastIsland.getIslandData("location");
        if (location == null) {
            return newIslandLocation(distance);
        }

        Location newLocation;

        for (BlockFace blockFace : LocationUtil.radial) {
            newLocation = location.getBlock().getRelative(blockFace, distance).getLocation();

            if (SkyBlockApi.getInstance().getIsland(newLocation) == null) {
                return newLocation;
            }
        }

        return newIslandLocation(distance);
    }

    public Location newIslandLocation(int distance, @NonNull SkyIsland skyIsland) {
        Location location = newIslandLocation(distance);
        skyIsland.addIslandData("location", location);

        return location;
    }

    public Location getIslandLocation(SkyIsland skyIsland) {
        if (skyIsland == null)
            return null;

        return skyIsland.getIslandData("location");
    }


    public int getIslandBorder(@NonNull SkyIsland skyIsland) {
        return skyIsland.getIslandData("upgrades_islandborderupgrade", 1) * 30;
    }


    @SneakyThrows
    public void deleteIsland(@NonNull SkyIsland skyIsland) {
        if (skyIsland.isEmpty())
            return;

        Location center = getIslandLocation(skyIsland).clone();
        center.setY(0);

        int borderRadius = getIslandBorder(skyIsland) / 2;

        Bukkit.getScheduler().runTask(SkyBlockApi.getInstance().getPlugin(), () -> {
            CuboidRegion islandCuboid = new CuboidRegion(
                    center.clone().add(borderRadius, 255, borderRadius),
                    center.clone().subtract(borderRadius, 0, borderRadius)
            );

            islandCuboid.forEachBlock(block -> {
                if (block.isEmpty()) {
                    return;
                }

                block.setTypeIdAndData(0, (byte) 0, false);
            });
        });

        Files.deleteIfExists(skyIsland.getConfigurationPath());
        skyIsland.getSkyPlayer().setIsland(null);
    }

}
