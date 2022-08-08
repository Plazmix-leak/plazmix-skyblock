package net.plazmix.skyblock.oneblock.upgrade;

import lombok.Getter;
import net.plazmix.skyblock.api.island.upgrade.IslandUpgrade;
import net.plazmix.skyblock.api.island.upgrade.IslandUpgradeManager;
import net.plazmix.skyblock.oneblock.upgrade.type.IslandBorderUpgrade;

import java.util.LinkedList;
import java.util.List;

@Getter
public final class OneblockUpgradeManager implements IslandUpgradeManager {

    private final List<IslandUpgrade> islandUpgrades = new LinkedList<>();
    private boolean initialized;

    @Override
    public void initialize() {
        this.initialized = true;

        islandUpgrades.add(new IslandBorderUpgrade());
        //...
    }

}
