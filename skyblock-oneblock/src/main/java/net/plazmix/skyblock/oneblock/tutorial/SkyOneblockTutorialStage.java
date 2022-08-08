package net.plazmix.skyblock.oneblock.tutorial;

import lombok.NonNull;
import org.bukkit.scheduler.BukkitRunnable;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.player.SkyPlayer;

import java.util.Collection;
import java.util.LinkedList;

public abstract class SkyOneblockTutorialStage {

    private final Collection<Runnable> commands = new LinkedList<>();
    private BukkitRunnable bukkitRunnable;


    public void addCommand(@NonNull Runnable command) {
        commands.add(command);
    }

    protected abstract void onEnable(@NonNull SkyPlayer skyPlayer);
    protected abstract void onDisable(@NonNull SkyPlayer skyPlayer);


    public void enable(@NonNull SkyPlayer skyPlayer) {
        if (bukkitRunnable != null) {
            return;
        }

        onEnable(skyPlayer);
        bukkitRunnable = new BukkitRunnable() {

            @Override
            public void run() {
                if (skyPlayer.getBukkitHandle() == null || !skyPlayer.getBukkitHandle().isOnline()) {
                    cancel();
                    return;
                }

                for (Runnable command : commands)
                    command.run();
            }
        };

        bukkitRunnable.runTaskTimer(SkyBlockApi.getInstance().getPlugin(), 0, 20);
    }

    public void disable(@NonNull SkyPlayer skyPlayer) {
        if (bukkitRunnable == null) {
            return;
        }

        bukkitRunnable.cancel();
        bukkitRunnable = null;

        onDisable(skyPlayer);
    }

}
