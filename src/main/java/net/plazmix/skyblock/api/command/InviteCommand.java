package net.plazmix.skyblock.api.command;

import org.bukkit.entity.Player;
import net.plazmix.command.BaseCommand;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.player.SkyPlayer;

public class InviteCommand extends BaseCommand<Player> {

    public InviteCommand() {
        super("invite");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        if (args.length == 0) {
            sendHelpMessage(player);
            return;
        }

        SkyPlayer skyPlayer = SkyBlockApi.getInstance().getPlayer(player);

        if (skyPlayer.getIsland() != null) {
            player.sendMessage("§cОшибка, у Вас уже имеется свой остров!");
            return;
        }

        if (skyPlayer.getInvitationIsland() == null) {
            player.sendMessage("§cОшибка, на данный момент у Вас нет активного приглашения на остров!");
            return;
        }

        switch (args[0].toLowerCase()) {

            case "apply":
            case "accept":
            case "принять": {

                skyPlayer.acceptInvite();
                break;
            }

            case "reject":
            case "deny":
            case "decline": {

                skyPlayer.rejectInvite();
                break;
            }

            default: {

                sendHelpMessage(player);
            }
        }
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage("§6§lOneBlock §8:: §fСписок доступных команд:");
        player.sendMessage(" §aПринять §7приглашение игрока - §e/invite accept");
        player.sendMessage(" §cОтклонить §7приглашение игрока - §e/invite decline");
    }
}
