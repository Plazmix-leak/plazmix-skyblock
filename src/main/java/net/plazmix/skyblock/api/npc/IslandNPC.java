package net.plazmix.skyblock.api.npc;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import net.plazmix.lobby.npc.ServerPlayerNPC;
import net.plazmix.protocollib.entity.impl.FakePlayer;

public class IslandNPC extends ServerPlayerNPC {

    public IslandNPC(Location location) {
        super("skywars_hypixel", location);
    }

    @Override
    protected void onReceive(@NonNull FakePlayer fakePlayer) {
        addHolographicLine("§a§lУправление островом");
        addHolographicLine("§7§oНажмите, чтобы открыть!");

        getHandle().setGlowingColor(ChatColor.GREEN);
        getHandle().setClickAction(player -> Bukkit.dispatchCommand(player, "is"));

        enableAutoLooking(5);
    }

}
