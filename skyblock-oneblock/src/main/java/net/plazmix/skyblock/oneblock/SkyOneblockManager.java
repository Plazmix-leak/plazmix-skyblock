package net.plazmix.skyblock.oneblock;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.skyblock.api.player.SkyPlayer;
import net.plazmix.skyblock.api.util.IslandUtil;
import net.plazmix.skyblock.oneblock.event.IslandLevelUpEvent;
import net.plazmix.skyblock.oneblock.event.TutorialEndEvent;
import net.plazmix.skyblock.oneblock.tutorial.SkyOneblockTutorial;

import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class SkyOneblockManager {

    public static final int MAX_ISLAND_RADIUS       = 300;
    public static final int MAX_ISLANDS_DISTANCE    = (MAX_ISLAND_RADIUS * 2);

    public static final SkyOneblockManager INSTANCE = new SkyOneblockManager();


    @Getter
    private final Map<String, SkyOneblockTutorial> playerTutorialMap = new HashMap<>();

    public void startTutorial(@NonNull Player player) {
        SkyPlayer skyPlayer = SkyBlockApi.getInstance().getPlayer(player);

        SkyOneblockTutorial skyOneblockTutorial = new SkyOneblockTutorial(skyPlayer);
        skyOneblockTutorial.start();

        skyOneblockTutorial.setOnEnd(() -> {

            Bukkit.getPluginManager().callEvent(new TutorialEndEvent(player));
            playerTutorialMap.remove(player.getName().toLowerCase());
        });

        playerTutorialMap.put(player.getName().toLowerCase(), skyOneblockTutorial);
    }

    public boolean isInTutorial(@NonNull Player player) {
        return playerTutorialMap.containsKey(player.getName().toLowerCase());
    }



    public void breakIslandBlock(@NonNull Player player, @NonNull SkyIsland skyIsland) {
        Block block = IslandUtil.getIslandLocation(skyIsland).getBlock();

        block.getWorld().spigot()
                .playEffect(block.getLocation().clone().add(0.5, 0.5, 0.5), Effect.CLOUD, 0, 0, 0.1F, 0.1F, 0.1F, 0.05F, 5, 50);

        updateIslandBlock(skyIsland);
    }


    private final TIntObjectMap<MaterialData[]> islandLevelTypesMap
            = new TIntObjectHashMap<MaterialData[]>() {{

        put(0, new MaterialData[]{new MaterialData(Material.DIRT), new MaterialData(Material.GRASS), new MaterialData(Material.LOG), new MaterialData(Material.WOOL)});
        put(1, new MaterialData[]{new MaterialData(Material.STONE), new MaterialData(Material.COBBLESTONE), new MaterialData(Material.COAL_ORE), new MaterialData(Material.STONE, (byte) 1), new MaterialData(Material.STONE, (byte) 3), new MaterialData(Material.STONE, (byte) 5)});
        put(2, new MaterialData[]{new MaterialData(Material.IRON_ORE), new MaterialData(Material.WOOD), new MaterialData(Material.CLAY), new MaterialData(Material.MELON_BLOCK)});
        put(3, new MaterialData[]{new MaterialData(Material.GOLD_ORE), new MaterialData(Material.LOG_2), new MaterialData(Material.CONCRETE)});
        put(5, new MaterialData[]{new MaterialData(Material.LAPIS_ORE), new MaterialData(Material.REDSTONE_ORE), new MaterialData(Material.NETHERRACK), new MaterialData(Material.SOUL_SAND)});
        put(8, new MaterialData[]{new MaterialData(Material.SNOW_BLOCK), new MaterialData(Material.PACKED_ICE)});
        put(10, new MaterialData[]{new MaterialData(Material.DIAMOND_ORE), new MaterialData(Material.BRICK)});
    }};

    private void updateIslandBlock(@NonNull SkyIsland skyIsland) {
        List<MaterialData> levelTypes = new ArrayList<>();

        int blocks = skyIsland.getIslandData("blocks", 0) + 1;
        skyIsland.addIslandData("blocks", blocks);

        int oldLevel = (blocks - 1) / 100;
        int newLevel = blocks / 100;

        if (newLevel > 0 && oldLevel < newLevel) {
            Bukkit.getPluginManager().callEvent(new IslandLevelUpEvent(skyIsland.getSkyPlayer().getBukkitHandle(), skyIsland, oldLevel, newLevel));
            return;
        }

        for (int i = newLevel; i >= 0; i--)
            if (islandLevelTypesMap.containsKey(i))
                levelTypes.addAll( Arrays.asList(islandLevelTypesMap.get(i)) );


        Block block = IslandUtil.getIslandLocation(skyIsland).getBlock();
        MaterialData materialData = levelTypes.stream().skip((long) (levelTypes.size() * Math.random())).findFirst().orElse(Material.GRASS.getNewData((byte) 0));

        block.setType(materialData.getItemType());
        block.setData(materialData.getData());
    }

}
