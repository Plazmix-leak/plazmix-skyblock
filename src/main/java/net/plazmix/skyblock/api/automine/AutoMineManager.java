package net.plazmix.skyblock.api.automine;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.updater.SimpleHolographicUpdater;
import net.plazmix.lobby.npc.ServerPlayerNPC;
import net.plazmix.lobby.npc.manager.ServerNPCManager;
import net.plazmix.protocollib.entity.impl.FakePlayer;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.PercentUtil;
import net.plazmix.utility.location.region.CuboidRegion;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class AutoMineManager {

    public static final AutoMineManager INSTANCE = new AutoMineManager();

    @Getter
    @Setter
    private CuboidRegion cuboidRegion;


    public void registerAutoMine(@NonNull Plugin plugin, @NonNull Location npcLocation) {
        if (cuboidRegion == null) {
            return;
        }

        AtomicLong nextMineUpdateMillis = new AtomicLong(-1);

        new BukkitRunnable() {

            private final Map<MaterialData, Double> automineBlocksMap
                    = new HashMap<MaterialData, Double>() {{

                // Тут в сумме 100% выходит, поэтому просто так проценты
                // лучше не корректировать

                put(new MaterialData(Material.STONE), 65.0d);
                put(new MaterialData(Material.COAL_ORE), 10.0d);
                put(new MaterialData(Material.IRON_ORE), 8.0d);
                put(new MaterialData(Material.LAPIS_ORE), 7.0d);
                put(new MaterialData(Material.GOLD_ORE), 5.0d);
                put(new MaterialData(Material.REDSTONE_ORE), 3.0d);
                put(new MaterialData(Material.EMERALD_ORE), 1.0d);
                put(new MaterialData(Material.DIAMOND_ORE), 1.0d);
            }};

            @Override
            public void run() {
                nextMineUpdateMillis.set(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));

                // Обновляем автошахту
                cuboidRegion.forEachBlock(block -> {
                    MaterialData materialData = automineBlocksMap.keySet().stream()
                            .filter(data -> PercentUtil.acceptRandomPercent(automineBlocksMap.get(data)))
                            .findFirst()
                            .orElse(new MaterialData(Material.STONE));

                    block.setTypeIdAndData(materialData.getItemTypeId(), materialData.getData(), false);
                });

                for (Player player : Bukkit.getOnlinePlayers()) {

                    // Если игрок застрял в шахте, то кидаем его вверх
                    if (cuboidRegion.contains(player.getLocation())) {

                        Location playerLocation = player.getLocation().clone();
                        playerLocation.setY(cuboidRegion.stream().max(Comparator.comparingInt(Block::getY)).orElse(null).getY() + 1);

                        player.teleport(playerLocation);
                    }

                    // Ну и отсылаем всем игрокам алерт
                    player.sendMessage("§6§lOneBlock §8:: §c§lВНИМАНИЕ! §fРесурсы в автошахте были обновлены, пора копать!");
                }
            }

        }.runTaskTimer(plugin, 0, 20 * 60 * 5);

        // Заспавним еще NPC, который типа заправляет этим шахтерским бизнесом
        ServerNPCManager.INSTANCE.register(new ServerPlayerNPC("Miner", npcLocation) {

            @Override
            protected void onReceive(@NonNull FakePlayer fakePlayer) {
                addHolographicLine("§eАвтоматическая шахта");
                addHolographicLine("§fОбновляется каждые §c5 минут");
                addHolographicLine("");

                addHolographicLine("§7Следующее обновление через:");
                addHolographicLine(ChatColor.GOLD + "...");

                holographic.setHolographicUpdater(10, new SimpleHolographicUpdater(holographic) {

                    @Override
                    public void accept(ProtocolHolographic protocolHolographic) {
                        if (nextMineUpdateMillis.get() <= 0) {
                            return;
                        }

                        String time = ChatColor.GOLD + NumberUtil.getTime(nextMineUpdateMillis.get() - System.currentTimeMillis());

                        if (ChatColor.stripColor(time).isEmpty()) {
                            time = (ChatColor.GREEN + "Обновление шахты...");
                        }

                        protocolHolographic.setOriginalHolographicLine(4, time);
                    }
                });

                fakePlayer.setGlowingColor(ChatColor.YELLOW);
                enableAutoLooking(8);
            }
        });
    }
}
