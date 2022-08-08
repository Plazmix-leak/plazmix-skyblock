package net.plazmix.skyblock.api;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.plazmix.PlazmixApi;
import net.plazmix.skyblock.api.command.InviteCommand;
import net.plazmix.skyblock.api.command.IslandCommand;
import net.plazmix.skyblock.api.command.SpawnCommand;
import net.plazmix.skyblock.api.listener.SkyPlayerListener;
import net.plazmix.skyblock.api.util.SkyblockSpawnUtil;

import java.io.File;
import java.util.Objects;

/*  Leaked by https://t.me/leak_mine
    - Все слитые материалы вы используете на свой страх и риск.

    - Мы настоятельно рекомендуем проверять код плагинов на хаки!
    - Список софта для декопиляции плагинов:
    1. Luyten (последнюю версию можно скачать можно тут https://github.com/deathmarine/Luyten/releases);
    2. Bytecode-Viewer (последнюю версию можно скачать можно тут https://github.com/Konloch/bytecode-viewer/releases);
    3. Онлайн декомпиляторы https://jdec.app или http://www.javadecompilers.com/

    - Предложить свой слив вы можете по ссылке @leakmine_send_bot или https://t.me/leakmine_send_bot
*/

@Getter
public abstract class SkyBlock
        extends JavaPlugin {

    private final String islandsWorldName = "IslandWorld";

    @Override
    public void onEnable() {
        SkyBlockApi.getInstance().setPlugin(this);

        // Islands world
        World islandWorld = createVoidWorld();
        islandWorld.getWorldBorder().reset();

        // Spawn world
        enableSpawnTicker();

        // Register commands.
        PlazmixApi.registerCommand(new IslandCommand());
        PlazmixApi.registerCommand(new InviteCommand());
        PlazmixApi.registerCommand(new SpawnCommand());

        // Register events.
        getServer().getPluginManager().registerEvents(new SkyPlayerListener(), this);

        // Offline islands load
        getLogger().info(ChatColor.GREEN + "[SkyBlock] Loading players islands...");
        int islandCounter = 0;

        for (File file : Objects.requireNonNull(getDataFolder().listFiles())) {
            if (file.isDirectory())
                continue;

            SkyBlockApi.getInstance().getPlayer(file.getName().substring(0, file.getName().length() - 4)).inject();
            islandCounter++;
        }

        getLogger().info(ChatColor.GREEN + "[SkyBlock] Loaded " + islandCounter + " islands!");
    }

    @Override
    public void onDisable() {

        // Online islands save
        for (Player player : Bukkit.getOnlinePlayers()) {
            SkyBlockApi.getInstance().getPlayer(player).save();
        }
    }

    protected World createVoidWorld() {
        return WorldCreator.name(islandsWorldName)
                .generator(new EmptyWorldGenerator())
                .createWorld();
    }

    private void enableSpawnTicker() {
        new BukkitRunnable() {

            @Override
            public void run() {
                Location spawnLocation = SkyblockSpawnUtil.getSpawnLocation();

                if (spawnLocation == null) {
                    return;
                }

                spawnLocation.getWorld().setSpawnLocation(spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ());

                spawnLocation.getWorld().setDifficulty(Difficulty.PEACEFUL);

                spawnLocation.getWorld().setTime(0);
                spawnLocation.getWorld().setFullTime(0);

                spawnLocation.getWorld().setWeatherDuration(0);

                spawnLocation.getWorld().setStorm(false);
                spawnLocation.getWorld().setThundering(false);
            }

        }.runTaskTimer(this, 1, 1);

        for (World otherWorld : Bukkit.getWorlds()) {
            otherWorld.setGameRuleValue("announceAdvancements", "false");

            if (otherWorld.getName().equalsIgnoreCase("Spawn"))
                continue;

            otherWorld.setPVP(true);
            otherWorld.setAutoSave(true);

            otherWorld.setDifficulty(Difficulty.HARD);
        }
    }

    public abstract void handleIslandCreation(@NonNull Player player);
    public abstract void handleIslandDeletion(@NonNull Player player);

    public abstract void handleIslandInviteCallback(@NonNull Player player);

}
