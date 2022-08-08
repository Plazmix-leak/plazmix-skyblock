package net.plazmix.skyblock.api.pvp;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.updater.SimpleHolographicUpdater;
import net.plazmix.lobby.npc.ServerPlayerNPC;
import net.plazmix.lobby.npc.manager.ServerNPCManager;
import net.plazmix.protocollib.entity.impl.FakePlayer;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.location.region.CuboidRegion;

public final class BattleArenaManager {

    public static final BattleArenaManager INSTANCE = new BattleArenaManager();

    @Getter
    @Setter
    private CuboidRegion cuboidRegion;


    public void registerBattleArena(@NonNull Location npcLocation) {
        if (cuboidRegion == null) {
            return;
        }

        ServerNPCManager.INSTANCE.register(new ServerPlayerNPC("Insultins", npcLocation) {

            @Override
            protected void onReceive(@NonNull FakePlayer fakePlayer) {

                addHolographicLine("§eБоевая арена");
                addHolographicLine("§fБудьте осторожны, переступив за");
                addHolographicLine("§cкрасную линию§f, Вы станете уязвимы!");
                addHolographicLine("");
                addHolographicLine("§7Сейчас на арене:");
                addHolographicLine("§6...");

                holographic.setHolographicUpdater(10, new SimpleHolographicUpdater(holographic) {

                    @Override
                    public void accept(ProtocolHolographic protocolHolographic) {
                        int playersInArena = (int) fakePlayer.getLocation().getWorld()
                                .getEntitiesByClasses(Player.class)
                                .stream()
                                .filter(entity -> cuboidRegion.contains(entity.getLocation()))
                                .count();

                        holographic.setOriginalHolographicLine(5, "§6"
                                + NumberUtil.formattingSpaced(playersInArena, "игрок", "игрока", "игроков"));
                    }
                });

                fakePlayer.setGlowingColor(ChatColor.RED);
                enableAutoLooking(8);
            }

        });
    }

}
