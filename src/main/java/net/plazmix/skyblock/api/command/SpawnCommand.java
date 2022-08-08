package net.plazmix.skyblock.api.command;

import org.bukkit.entity.Player;
import net.plazmix.command.BaseCommand;
import net.plazmix.skyblock.api.util.SkyblockSpawnUtil;

public class SpawnCommand extends BaseCommand<Player> {

    public SpawnCommand() {
        super("spawn", "спавн", "ызфцт");
    }

    @Override
    protected void onExecute(Player player, String[] args) {

        if (SkyblockSpawnUtil.getSpawnLocation() != null) {
            player.teleport(SkyblockSpawnUtil.getSpawnLocation());
        }
    }

}
