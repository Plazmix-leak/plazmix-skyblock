package net.plazmix.skyblock.oneblock;

import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import net.plazmix.PlazmixApi;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.impl.SimpleHolographic;
import net.plazmix.lobby.npc.manager.ServerNPCManager;
import net.plazmix.lobby.playertop.PlayerTopsStorage;
import net.plazmix.lobby.playertop.database.type.PlayerTopsCacheDatabase;
import net.plazmix.lobby.playertop.database.type.PlayerTopsMysqlConvertibleDatabase;
import net.plazmix.lobby.playertop.pagination.PlayerTopsPaginationChanger;
import net.plazmix.skyblock.api.SkyBlock;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.advancement.SkyAdvancementManager;
import net.plazmix.skyblock.api.automine.AutoMineManager;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.skyblock.api.island.upgrade.IslandUpgradeMenu;
import net.plazmix.skyblock.api.npc.AuctionNPC;
import net.plazmix.skyblock.api.npc.IslandNPC;
import net.plazmix.skyblock.api.npc.ShopNPC;
import net.plazmix.skyblock.api.pvp.BattleArenaManager;
import net.plazmix.skyblock.api.util.SkyblockSpawnUtil;
import net.plazmix.skyblock.oneblock.advancement.*;
import net.plazmix.skyblock.oneblock.listener.*;
import net.plazmix.skyblock.oneblock.scoreboard.OneblockScoreboard;
import net.plazmix.skyblock.oneblock.upgrade.OneblockUpgradeManager;
import net.plazmix.utility.PlayerUtil;
import net.plazmix.utility.leveling.LevelingUtil;
import net.plazmix.utility.location.region.CuboidRegion;

public final class SkyOneblockPlugin
        extends SkyBlock {

    @Override
    public void onEnable() {
        super.onEnable();
        registerNpcs();

        // Register event listeners.
        getServer().getPluginManager().registerEvents(new SkyOneblockListener(), this);
        getServer().getPluginManager().registerEvents(new TutorialListener(), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
        getServer().getPluginManager().registerEvents(new IslandSettingsListener(), this);

        // Register advancements.
        registerAdvancements();

        // Islands upgrades
        IslandUpgradeMenu.setUpgradeManager(new OneblockUpgradeManager());

        // Set spawn location.
        SkyblockSpawnUtil.setSpawnLocation(new Location(getServer().getWorld("Spawn"), 0.5, 52, -29.5, 0, 0));


        // Create auto mine.
        addAutoResettableMine();

        // Create battle arena.
        addBattleArena();

        // Add server tops.
        addTops();

        // Add portal arrow holographic
        addPortalArrow();
    }

    @Override
    public void handleIslandCreation(@NonNull Player player) {
        SkyOneblockManager.INSTANCE.startTutorial(player);
    }

    @Override
    public void handleIslandDeletion(@NonNull Player player) {
        new OneblockScoreboard(player);
    }

    @Override
    public void handleIslandInviteCallback(@NonNull Player player) {
        handleIslandDeletion(player);
    }

    private void registerNpcs() {
        World world = getServer().getWorld("Spawn");

        ServerNPCManager.INSTANCE.register(new IslandNPC(new Location(world, 13, 51, -13, 90, 0)));
        ServerNPCManager.INSTANCE.register(new ShopNPC(new Location(world, 13, 51, -9, 90, 0)));
        ServerNPCManager.INSTANCE.register(new AuctionNPC(new Location(world, 13, 51, -5, 90, 0)));
    }

    private void registerAdvancements() {
        SkyAdvancementManager advancementManager = SkyBlockApi.getInstance().getAdvancementManager();

        advancementManager.registerAdvancement(new CheckAutomineAdvancement());
        advancementManager.registerAdvancement(new CheckBattleArenaAdvancement());
        advancementManager.registerAdvancement(new TutorialEndAdvancement());
        advancementManager.registerAdvancement(new FirstDeathAdvancement());
        advancementManager.registerAdvancement(new IslandLevelupAdvancement());
    }

    private void addAutoResettableMine() {
        World world = SkyblockSpawnUtil.getSpawnLocation().getWorld();

        CuboidRegion cuboidRegion = new CuboidRegion (
                new Location(world, -37, 49, -14),
                new Location(world, -28, 39, 3)
        );

        AutoMineManager.INSTANCE.setCuboidRegion(cuboidRegion);
        AutoMineManager.INSTANCE.registerAutoMine(this, new Location(world, -18.5, 50, -13.5, -45, 0));
    }

    private void addBattleArena() {
        World world = SkyblockSpawnUtil.getSpawnLocation().getWorld();

        CuboidRegion cuboidRegion = new CuboidRegion (
                new Location(world, 29, 40, 60),
                new Location(world, -37, 70, 107)
        );

        BattleArenaManager.INSTANCE.setCuboidRegion(cuboidRegion);
        BattleArenaManager.INSTANCE.registerBattleArena(new Location(world, 9.5, 49, 57.5, 165, 0));
    }

    private void addTops() {
        World world = SkyblockSpawnUtil.getSpawnLocation().getWorld();
        Location location = new Location(world, 35.5, 56, 2.5);

        PlayerTopsPaginationChanger paginationChanger = PlayerTopsPaginationChanger.create();

        paginationChanger.addPlayerTops(PlayerTopsStorage.newBuilder()

                .setDatabaseManager(new PlayerTopsCacheDatabase(PlazmixUser -> {
                    SkyIsland skyIsland = SkyBlockApi.getInstance().getIsland(PlazmixUser.getName());

                    if (skyIsland == null) {
                        return 0;
                    }

                    return skyIsland.getIslandData("blocks", 0);
                }))

                .setLocation(location)
                .setSkullParticle(Particle.TOTEM)

                .setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDUyOGVkNDU4MDI0MDBmNDY1YjVjNGUzYTZiN2E5ZjJiNmE1YjNkNDc4YjZmZDg0OTI1Y2M1ZDk4ODM5MWM3ZCJ9fX0=")

                .setLimit(10)
                .setUpdater(60)

                .setStatsName("Острова")
                .setDescription("Топ 10 игроков, набравшие наибольшее",
                        "количество сломанных блоков")

                .setValueSuffix("блоков"));

        paginationChanger.addPlayerTops(PlayerTopsStorage.newBuilder()
                .setDatabaseManager(new PlayerTopsCacheDatabase(PlazmixUser -> Math.toIntExact(PlayerUtil.getOfflinePlayerStatistic(Bukkit.getOfflinePlayer(PlazmixUser.getName()), Statistic.PLAYER_KILLS))))

                .setLocation(location)
                .setSkullParticle(Particle.FLAME)

                .setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMwOTNhNWI3NzY0MmQ0MDkyMTEyZjQ2ZWE2ODE0MGZiNWFlMDRiYmQyMzFjZGExMDY2YTA0YzE4Yjg5Yzk0ZSJ9fX0=")

                .setLimit(10)
                .setUpdater(60)

                .setStatsName("Убиства")
                .setDescription("Топ 10 игроков, набравшие наибольшее",
                        "количество убиств")

                .setValueSuffix("убиств"));

        paginationChanger.addPlayerTops(PlayerTopsStorage.newBuilder()
                .setDatabaseManager(new PlayerTopsMysqlConvertibleDatabase("PlayerLeveling", "Experience", (PlazmixUser, value) -> LevelingUtil.getLevel(value)))

                .setLocation(location)
                .setSkullParticle(Particle.FIREWORKS_SPARK)

                .setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTMxYTRmYWIyZjg3ZGI1NDMzMDEzNjUxN2I0NTNhYWNiOWQ3YzBmZTc4NDMwMDcwOWU5YjEwOWNiYzUxNGYwMCJ9fX0=")

                .setLimit(10)
                .setUpdater(60)

                .setStatsName("Уровень")
                .setDescription("Топ 10 игроков, набравшие наибольшее",
                        "количество игрового уровня")

                .setValueSuffix("уровень"));

        paginationChanger.spawn();
    }

    private void addPortalArrow() {
        World world = SkyblockSpawnUtil.getSpawnLocation().getWorld();
        Location location = new Location(world, 0.5, 53, 8.5);

        ProtocolHolographic protocolHolographic = new SimpleHolographic(location);

        protocolHolographic.addOriginalHolographicLine("§b§l§m▉▉▉");
        protocolHolographic.addOriginalHolographicLine("§b§l§m▉▉▉");
        protocolHolographic.addOriginalHolographicLine("§b§l§m▉▉▉");
        protocolHolographic.addOriginalHolographicLine("§b§l§m▉▉▉");
        protocolHolographic.addOriginalHolographicLine("§b§l§m▉▉▉");
        protocolHolographic.addOriginalHolographicLine("§b§l§m▉▉▉");
        protocolHolographic.addOriginalHolographicLine("§b§l§m▉▉▉");
        protocolHolographic.addOriginalHolographicLine("§b§l§m▉▉▉▉▉▉▉");
        protocolHolographic.addOriginalHolographicLine("§b§l§m▉▉▉▉▉");
        protocolHolographic.addOriginalHolographicLine("§b§l§m▉▉▉");
        protocolHolographic.addOriginalHolographicLine("§b§l§m▉");

        protocolHolographic.spawn();

        Vector vectorUp     = new Vector(0, 2, 0);
        Vector vectorDown   = new Vector(0, -2, 0);

        new BukkitRunnable() {
            private boolean isUp;

            @Override
            public void run() {

                Location locationTo = location.clone().add((isUp) ? vectorUp : vectorDown);

                if (protocolHolographic.getLocation().distance(locationTo) <= 1) {
                    this.isUp = !isUp;
                }

                protocolHolographic.teleport(protocolHolographic.getLocation().clone().add(0, (isUp ? 0.1 : -0.1), 0));
            }

        }.runTaskTimer(this, 1, 1);
    }

}
