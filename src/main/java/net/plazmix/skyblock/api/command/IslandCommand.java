package net.plazmix.skyblock.api.command;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.plazmix.command.BaseCommand;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.island.IslandMenu;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.skyblock.api.player.SkyPlayer;
import net.plazmix.skyblock.api.settings.IslandSettings;
import net.plazmix.skyblock.api.util.IslandUtil;
import net.plazmix.skyblock.api.util.SkyblockSpawnUtil;
import net.plazmix.utility.ChatUtil;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.PlayerUtil;
import net.plazmix.utility.player.PlazmixUser;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class IslandCommand extends BaseCommand<Player> {

    public IslandCommand() {
        super("is", "island", "ob", "oneblock", "sb", "skyblock");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        SkyPlayer skyPlayer = SkyBlockApi.getInstance().getPlayer(player);
        SkyIsland skyIsland = skyPlayer.getIsland();

        if (args.length == 0) {
            sendHelpMessage(player);

            return;
        }

        switch (args[0].toLowerCase()) {

            case "leave": {
                if (skyPlayer.getIsland() == null) {
                    player.sendMessage("§cОшибка, у Вас нет своего острова! Чтобы создать остров, пишите - /is create");
                    break;
                }

                if (skyIsland.getSkyPlayer().getName().equalsIgnoreCase(player.getName())) {
                    player.sendMessage("§cОшибка, Вы не можете покинуть свой же остров!");
                    return;
                }

                if (SkyblockSpawnUtil.getSpawnLocation() != null) {
                    player.teleport(SkyblockSpawnUtil.getSpawnLocation());
                }

                skyIsland.alertMessage(true, "§6§lOneBlock §8:: §fУчастник острова " + skyPlayer.getHandle().getDisplayName() + " §fпокинул Вас!");
                skyIsland.removeMember(skyPlayer.getName());

                SkyBlockApi.getInstance().getPlugin().handleIslandInviteCallback(player);
                break;
            }

            case "tp":
            case "teleport": {
                if (args.length < 2) {
                    player.sendMessage("§cОшибка, пишите - /is teleport <ник игрока>");
                    break;
                }

                Player targetPlayer = Bukkit.getPlayer(args[1]);

                if (targetPlayer == null) {
                    player.sendMessage("§cОшибка, данный игрок не в сети!");
                    break;
                }

                if (targetPlayer.equals(player)) {
                    player.sendMessage("§cОшибка, для телепортации на свой остров используйте - /is home");
                    break;
                }

                SkyIsland targetIsland = SkyBlockApi.getInstance().getIsland(targetPlayer.getName());

                if (targetIsland == null) {
                    player.sendMessage("§cОшибка, данный игрок еще не создал свой остров!");
                    break;
                }

                if (!IslandSettings.TELEPORT.get(targetIsland)) {
                    player.sendMessage("§cОшибка, данный игрок отключил телепортации на свой остров!");
                    break;
                }

                player.teleport(IslandUtil.getIslandLocation(targetIsland).clone().add(0.5, 1, 0.5));
                targetIsland.createBorder(IslandUtil.getIslandBorder(targetIsland), player);
                break;
            }

            case "invite":
            case "a":
            case "add": {
                if (skyPlayer.getIsland() == null) {
                    player.sendMessage("§cОшибка, у Вас нет своего острова! Чтобы создать остров, пишите - /is create");
                    break;
                }

                if (args.length < 2) {
                    player.sendMessage("§cОшибка, пишите - /is add <ник игрока>");
                    break;
                }

                Player islandPlayer = Bukkit.getPlayer(args[1]);

                if (islandPlayer == null) {
                    player.sendMessage("§cОшибка, данный игрок не в сети!");
                    break;
                }

                if (islandPlayer.equals(player)) {
                    player.sendMessage("§cОшибка, Вы не можете приглашать на остров самого себя!");
                    break;
                }

                if (skyIsland.isMember(islandPlayer.getName())) {
                    player.sendMessage("§cОшибка, данный игрок уже является участником острова!");
                    break;
                }

                if (SkyBlockApi.getInstance().getIsland(islandPlayer.getName()) != null) {
                    player.sendMessage("§cОшибка, у данного игрока уже есь свой остров!");
                    break;
                }

                BaseComponent[] acceptButton = ChatUtil.newBuilder("§a§l[ПРИНЯТЬ]")
                        .setClickEvent(ClickEvent.Action.RUN_COMMAND, "/invite accept")
                        .setHoverEvent(HoverEvent.Action.SHOW_TEXT, "§aНажмите, чтобы принять")
                        .build();

                BaseComponent[] cancelButton = ChatUtil.newBuilder("§c§l[ОТКЛОНИТЬ]")
                        .setClickEvent(ClickEvent.Action.RUN_COMMAND, "/invite reject")
                        .setHoverEvent(HoverEvent.Action.SHOW_TEXT, "§cНажмите, чтобы отклонить")
                        .build();

                BaseComponent[] spacesButton = ChatUtil.newBuilder("         ").build();

                islandPlayer.spigot().sendMessage(TextComponent.fromLegacyText("§6§lOneBlock §8:: " + PlazmixUser.of(player).getDisplayName() + " §fпригласил Вас на свой остров!\n\n"));
                islandPlayer.spigot().sendMessage(ChatUtil.buildMessages(spacesButton, acceptButton, spacesButton, cancelButton, TextComponent.fromLegacyText("\n")));

                player.sendMessage("§6§lOneBlock §8:: §fПриглашение на остров было успешно отправлено " + PlazmixUser.of(islandPlayer).getDisplayName());
                SkyBlockApi.getInstance().getPlayer(islandPlayer).setInvitationIsland(skyIsland);
                break;
            }

            case "kick":
            case "r":
            case "rem":
            case "remove": {
                if (skyPlayer.getIsland() == null) {
                    player.sendMessage("§cОшибка, у Вас нет своего острова! Чтобы создать остров, пишите - /is create");
                    break;
                }

                if (args.length < 2) {
                    player.sendMessage("§cОшибка, пишите - /is remove <ник игрока>");
                    return;
                }

                if (args[1].equalsIgnoreCase(player.getName())) {
                    player.sendMessage("§cОшибка, Вы не можете удалять с острова самого себя!");
                    return;
                }

                if (!skyIsland.isMember(args[1])) {
                    player.sendMessage("§cОшибка, данный игрок не является участником острова!");
                    break;
                }

                if (Bukkit.getPlayer(args[1]) != null && SkyblockSpawnUtil.getSpawnLocation() != null) {
                    Bukkit.getPlayer(args[1]).teleport(SkyblockSpawnUtil.getSpawnLocation());
                }

                skyIsland.alertMessage(true, "§6§lOneBlock §8:: " + PlazmixUser.of(args[1]).getDisplayName() + " §fбыл кикнут с острова его создаталем!");
                skyIsland.removeMember(args[1]);
                break;
            }

            case "c":
            case "create": {
                if (skyPlayer.getIsland() != null) {
                    player.sendMessage("§cОшибка, у Вас уже есть остров!");
                    break;
                }

                SkyBlockApi.getInstance().getPlugin().handleIslandCreation(player);
                break;
            }

            case "d":
            case "del":
            case "delete": {
                if (skyPlayer.getIsland() == null) {
                    player.sendMessage("§cОшибка, у Вас нет своего острова! Чтобы создать остров, пишите - /is create");
                    break;
                }

                if (!skyIsland.getSkyPlayer().getName().equalsIgnoreCase(player.getName())) {
                    player.sendMessage("§cОшибка, остров может удалять только его создатель!");
                    return;
                }

                if (SkyblockSpawnUtil.getSpawnLocation() != null) {
                    player.teleport(SkyblockSpawnUtil.getSpawnLocation());
                }

                IslandUtil.deleteIsland(skyIsland);
                player.sendMessage("§6§lOneBlock §8:: §fВаш остров был успешно §cудален§f!");

                SkyBlockApi.getInstance().getPlugin().handleIslandDeletion(player);

                for (int memberId : skyIsland.getMembers()) {
                    Player memberPlayer = PlazmixUser.of(memberId).handle();

                    if (memberPlayer != null) {

                        if (SkyblockSpawnUtil.getSpawnLocation() != null) {
                            memberPlayer.teleport(SkyblockSpawnUtil.getSpawnLocation());
                        }

                        skyIsland.alertMessage(false, "§6§lOneBlock §8:: §fОстров " + PlayerUtil.getDisplayName(player) + " §fбыл §cудален §fсоздаталем!");
                        SkyBlockApi.getInstance().getPlugin().handleIslandDeletion(memberPlayer);
                    }
                }

                break;
            }

            case "m":
            case "menu": {
                if (skyPlayer.getIsland() == null) {
                    player.sendMessage("§cОшибка, у Вас нет своего острова! Чтобы создать остров, пишите - /is create");
                    break;
                }

                new IslandMenu().openInventory(player);
                break;
            }

            case "h":
            case "home": {
                if (skyPlayer.getIsland() == null) {
                    player.sendMessage("§cОшибка, у Вас нет своего острова! Чтобы создать остров, пишите - /is create");
                    break;
                }

                player.teleport(IslandUtil.getIslandLocation(skyIsland).clone().add(0.5, 1, 0.5));

                skyIsland.createBorder(IslandUtil.getIslandBorder(skyIsland));
                break;
            }

            case "t":
            case "top": {
                player.sendMessage("§6§lOneBlock §8:: §fТоп §a10 §fостровов по уровню:");

                int islandPlace = 1;
                for (SkyIsland topIsland : SkyBlockApi.getInstance().getPlayerMap()
                        .values()
                        .stream()

                        .filter(skyPlayer1 -> skyPlayer1.getIsland() != null && skyPlayer1.getIsland().getSkyPlayer().getName().equalsIgnoreCase(skyPlayer1.getName()))
                        .map(SkyPlayer::getIsland)

                        .sorted(Collections.reverseOrder(Comparator.comparing(island -> island.getIslandData("blocks", 0))))

                        .limit(10)
                        .collect(Collectors.toCollection(LinkedList::new))) {

                    int blockCount = topIsland.getIslandData("blocks", 0);

                    player.sendMessage("§e" + islandPlace + ". §fОстров " + topIsland.getSkyPlayer().getHandle().getDisplayName() + " §f- §b"
                            + (blockCount / 100) + " уровень §7(" + NumberUtil.getIntPercent(blockCount % 100, 100) + "%)");

                    islandPlace++;
                }

                break;
            }

            default: {
                sendHelpMessage(player);
            }
        }
    }

    private void sendHelpMessage(Player player) {
        if (SkyBlockApi.getInstance().getIsland(player.getName()) == null) {

            player.sendMessage("§cОшибка, у Вас нет своего острова! Чтобы создать остров, пишите - /is create");
            return;
        }

        player.sendMessage("§6§lOneBlock §8:: §fСписок доступных команд:");
        player.sendMessage(" §7Открыть меню острова - §e/is menu");
        player.sendMessage(" §7Удалить остров - §e/is delete");

        player.sendMessage(" §7Телепортироваться на свой остров - §e/is home");
        player.sendMessage(" §7Телепортироваться на чужой остров - §e/is teleport <ник игрока>");

        player.sendMessage(" §7Покинуть остров - §e/is leave");
        player.sendMessage(" §7Добавить участника на остров - §e/is invite <ник игрока>");
        player.sendMessage(" §7Удалить участника с острова - §e/is kick <ник игрока>");

        player.sendMessage(" §7Показать топ 15 островов - §e/is top");
    }

}
