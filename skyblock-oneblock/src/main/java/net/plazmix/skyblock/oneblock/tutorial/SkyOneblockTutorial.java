package net.plazmix.skyblock.oneblock.tutorial;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.skyblock.api.player.SkyPlayer;
import net.plazmix.skyblock.oneblock.tutorial.stage.TutorialBreakOneblockStage;
import net.plazmix.skyblock.oneblock.tutorial.stage.TutorialIslandLevelUpStage;
import net.plazmix.skyblock.oneblock.tutorial.stage.TutorialPlayerBuildingStage;
import net.plazmix.skyblock.oneblock.SkyOneblockManager;
import net.plazmix.skyblock.api.util.IslandUtil;

import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
public class SkyOneblockTutorial {

    private final SkyPlayer skyPlayer;

    private final List<SkyOneblockTutorialStage> stageCollection = new LinkedList<>();
    private int stageCounter = 0;

    @Setter
    private Runnable onEnd;

    {
        addStage(new TutorialBreakOneblockStage()); // NPC ломает oneblock острова
        addStage(new TutorialPlayerBuildingStage()); // Фейковое строительство острова на глазах у игрока
        addStage(new TutorialIslandLevelUpStage()); // Как прокачивать уровень oneblock острова
    }


    public void start() {
        SkyIsland island = new SkyIsland(skyPlayer);
        island.createIfNotExists();

        Location location = IslandUtil.newIslandLocation(SkyOneblockManager.MAX_ISLANDS_DISTANCE, island);
        location.getBlock().setType(Material.GRASS);

        skyPlayer.setIsland(island);
        island.createBorder(IslandUtil.getIslandBorder(island));

        skyPlayer.getBukkitHandle().setGameMode(GameMode.SPECTATOR);

        skyPlayer.getBukkitHandle().setFlySpeed(0);
        skyPlayer.getBukkitHandle().setWalkSpeed(0);


        new BukkitRunnable() {

            @Override
            public void run() {
                if (skyPlayer.getBukkitHandle() == null || !skyPlayer.getBukkitHandle().isOnline()) {
                    cancel();
                    return;
                }

                if (stageCounter >= stageCollection.size()) {

                    stageCollection.get(stageCounter - 1).disable(skyPlayer);
                    cancel();

                    if (onEnd != null) {
                        onEnd.run();
                    }

                    return;
                }

                nextStage();
                skyPlayer.getBukkitHandle().playSound(skyPlayer.getBukkitHandle().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }

        }.runTaskTimer(SkyBlockApi.getInstance().getPlugin(), 0, 20 * 10);
    }

    private void nextStage() {
        SkyOneblockTutorialStage currentStage = stageCollection.get(stageCounter);

        if (stageCounter > 0) {
            stageCollection.get(stageCounter - 1).disable(skyPlayer);
        }

        currentStage.enable(skyPlayer);
        stageCounter++;
    }

    private void addStage(@NonNull SkyOneblockTutorialStage tutorialStage) {
        stageCollection.add(tutorialStage);
    }

}
