package net.plazmix.skyblock.api.settings;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.utility.player.PlazmixUser;

@SuppressWarnings("all")
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum IslandSettings {

    BUILD(Material.GOLD_AXE,            "Строительство",      "settings_build"),
    USE(Material.CHEST,                 "Использование",      "settings_use"),
    TELEPORT(Material.ENDER_PEARL,      "Телепортация",       "settings_teleport",      Group.ABOBA, true),
    BATTLE(Material.WOOD_SWORD,         "PvP",                "settings_pvp",           Group.STAR, false),
    DAMAGE(Material.REDSTONE,           "Урон",               "settings_damage",        Group.UNIVERSE, true),
    PHYSICS(Material.GRAVEL,            "Физика",             "settings_physics",       Group.LUXURY, true),
    ANIMAL_SPAWN(Material.MONSTER_EGG,  "Появление животных", "settings_animal_spawn",  Group.COSMO, true),
    MONSTER_SPAWN(Material.MOB_SPAWNER, "Появление монстров", "settings_monster_spawn", Group.COSMO, true),
    ;


    @Getter
    Material iconType;

    @Getter
    String iconName, settingKey;

    // бесит, что просит поставить final
    @Getter
    @NonFinal
    Group minGroup = Group.ABOBA;

    // бесит, что просит поставить final
    @Getter
    @NonFinal
    boolean defValue = false;

    public boolean canUpdate(@NonNull Player player) {
        return PlazmixUser.of(player).getGroup().getLevel() >= minGroup.getLevel();
    }

    public void update(@NonNull SkyIsland island, Object value) {
        PlazmixUser plazmixUser = island.getSkyPlayer().getHandle();

        if (plazmixUser.handle() != null && !canUpdate(plazmixUser.handle())) {
            plazmixUser.handle().sendMessage("§cДанная настройка доступна от статуса " + minGroup.getPrefix() + " §cи выше!");
            return;
        }

        island.addIslandData(settingKey, value);
        island.saveIsland();
    }

    public boolean get(@NonNull SkyIsland island) {
        return island.getIslandData(settingKey, defValue);
    }

}
