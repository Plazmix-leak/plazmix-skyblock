package net.plazmix.skyblock.api.island.upgrade;

import java.util.List;

public interface IslandUpgradeManager {

    List<IslandUpgrade> getIslandUpgrades();


    boolean isInitialized();

    void initialize();
}
