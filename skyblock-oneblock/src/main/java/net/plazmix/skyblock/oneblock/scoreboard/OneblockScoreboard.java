package net.plazmix.skyblock.oneblock.scoreboard;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.plazmix.scoreboard.BaseScoreboardBuilder;
import net.plazmix.scoreboard.BaseScoreboardScope;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.skyblock.api.player.SkyPlayer;
import net.plazmix.utility.DateUtil;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.player.PlazmixUser;

public class OneblockScoreboard {

    public OneblockScoreboard(@NonNull Player player) {
        SkyPlayer skyPlayer = SkyBlockApi.getInstance().getPlayer(player);
        PlazmixUser PlazmixUser = skyPlayer.getHandle();

        SkyIsland island = skyPlayer.getIsland();

        BaseScoreboardBuilder scoreboardBuilder = BaseScoreboardBuilder.newScoreboardBuilder();

        // Initialize scoreboard.
        scoreboardBuilder.scoreboardDisplay(ChatColor.GOLD + (ChatColor.BOLD + "OneBlock"));
        scoreboardBuilder.scoreboardScope(BaseScoreboardScope.PROTOTYPE);

        // Scoreboard lines.
        if (island == null) {
            scoreboardBuilder.scoreboardLine(13, ChatColor.GRAY + "Oneblock " + DateUtil.formatPattern(DateUtil.DEFAULT_DATE_PATTERN));
            scoreboardBuilder.scoreboardLine(12, "");
            scoreboardBuilder.scoreboardLine(11, " §eПрофиль:");
            scoreboardBuilder.scoreboardLine(10, "  §fБаланс: §e" + NumberUtil.spaced(PlazmixUser.getCoins()));
            scoreboardBuilder.scoreboardLine(9, "  §fЗолото: §6" + NumberUtil.spaced(PlazmixUser.getGolds()));
            scoreboardBuilder.scoreboardLine(8, "  §fУровень: §d" + NumberUtil.spaced(PlazmixUser.getLevel()));
            scoreboardBuilder.scoreboardLine(7, "");
            scoreboardBuilder.scoreboardLine(6, " §eОстров: §cне создан");
            scoreboardBuilder.scoreboardLine(5, "  §fФаза острова: §b0 §70%");
            scoreboardBuilder.scoreboardLine(4, "  §fДостижений: §c0§f/§a0");
            scoreboardBuilder.scoreboardLine(3, "");
            scoreboardBuilder.scoreboardLine(2, " §fОнлайн режима: §a" + Bukkit.getOnlinePlayers().size());
            scoreboardBuilder.scoreboardLine(1, ChatColor.GOLD + "www.TynixCloud.ru");

        } else {

            scoreboardBuilder.scoreboardLine(15, ChatColor.GRAY + "Oneblock " + DateUtil.formatPattern(DateUtil.DEFAULT_DATE_PATTERN));
            scoreboardBuilder.scoreboardLine(14, "");
            scoreboardBuilder.scoreboardLine(13, " §eПрофиль:");
            scoreboardBuilder.scoreboardLine(12, "  §fБаланс: §e" + NumberUtil.spaced(PlazmixUser.getCoins()));
            scoreboardBuilder.scoreboardLine(11, "  §fЗолото: §6" + NumberUtil.spaced(PlazmixUser.getGolds()));
            scoreboardBuilder.scoreboardLine(10, "  §fУровень: §d" + NumberUtil.spaced(PlazmixUser.getLevel()));
            scoreboardBuilder.scoreboardLine(9, "");
            scoreboardBuilder.scoreboardLine(8, " §eОстров:");
            scoreboardBuilder.scoreboardLine(7, "  " + island.getSkyPlayer().getHandle().getDisplayName());
            scoreboardBuilder.scoreboardLine(6, "  §fФаза острова: §b0");
            scoreboardBuilder.scoreboardLine(5, "  §fСломано: §e0");
            scoreboardBuilder.scoreboardLine(4, "  §fУчастиков: §c0");
            scoreboardBuilder.scoreboardLine(3, "");
            scoreboardBuilder.scoreboardLine(2, "§fОнлайн сервера: §a" + Bukkit.getOnlinePlayers().size());
            scoreboardBuilder.scoreboardLine(1, ChatColor.GOLD + "www.TynixCloud.ru");

            // Scoreboard lines Updater.
            scoreboardBuilder.scoreboardUpdater((baseScoreboard, player1) -> {
                baseScoreboard.setScoreboardLine(12, player, "  §fБаланс: §e" + NumberUtil.spaced(PlazmixUser.getCoins()));
                baseScoreboard.setScoreboardLine(11, player, "  §fЗолото: §6" + NumberUtil.spaced(PlazmixUser.getGolds()));
                baseScoreboard.setScoreboardLine(10, player, "  §fУровень: §d" + NumberUtil.spaced(PlazmixUser.getLevel()));

                int blocks = island.getIslandData("blocks", 0);

                baseScoreboard.setScoreboardLine(7, player, "  " + island.getSkyPlayer().getHandle().getDisplayName());
                baseScoreboard.setScoreboardLine(6, player, "  §fФаза острова: §b" + (blocks / 100) + " §7" + NumberUtil.getIntPercent(blocks % 100, 100) + "%");
                baseScoreboard.setScoreboardLine(5, player, "  §fСломано: §e" + (blocks % 100) + "§7/§a100");
                baseScoreboard.setScoreboardLine(4, player, "  §fУчастиков: §c" + NumberUtil.spaced(island.getMembersCount()));

                baseScoreboard.setScoreboardLine(2, player, "§fОнлайн сервера: §a" + NumberUtil.spaced(Bukkit.getOnlinePlayers().size()));

            }, 20);
        }

        // Apply scoreboard to the player.
        scoreboardBuilder.build().setScoreboardToPlayer(player);
    }
}
