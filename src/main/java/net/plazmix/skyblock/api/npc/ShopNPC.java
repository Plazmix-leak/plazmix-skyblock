package net.plazmix.skyblock.api.npc;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import net.plazmix.lobby.npc.ServerPlayerNPC;
import net.plazmix.protocollib.entity.impl.FakePlayer;

public class ShopNPC extends ServerPlayerNPC {

    public ShopNPC(Location location) {
        super("__Sora", location);
    }

    @Override
    protected void onReceive(@NonNull FakePlayer fakePlayer) {
        addHolographicLine("§a§lМагазин предметов");
        addHolographicLine("§7§oНажмите, чтобы открыть!");

        getHandle().setGlowingColor(ChatColor.GREEN);
        getHandle().setClickAction(player -> Bukkit.dispatchCommand(player, "shop"));

        enableAutoLooking(5);
    }

}
