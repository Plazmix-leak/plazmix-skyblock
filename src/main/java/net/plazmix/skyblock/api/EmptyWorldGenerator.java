package net.plazmix.skyblock.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmptyWorldGenerator extends ChunkGenerator {

    public List<BlockPopulator> getDefaultPopulators(World world) {
        return new ArrayList<>();
    }

    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    public byte[] generate(World world, Random rand, int chunkX, int chunkZ) {
        return new byte[32768];
    }

    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, 100, 0);
    }

}
