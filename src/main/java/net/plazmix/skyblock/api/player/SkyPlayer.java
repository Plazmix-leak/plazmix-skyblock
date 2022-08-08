package net.plazmix.skyblock.api.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.plazmix.coreconnector.core.network.NetworkManager;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.island.SkyIsland;
import net.plazmix.utility.player.PlazmixUser;

import java.nio.file.Files;

@Getter
@Setter
@RequiredArgsConstructor
public class SkyPlayer {

    private final String name;

    public SkyIsland island;
    private SkyIsland invitationIsland;

    public SkyIsland getIsland() {
        return SkyBlockApi.getInstance().getIsland(this);
    }

    public void inject() {
        if (Files.exists( SkyBlockApi.getInstance().getPlayerIslandPath(name) )) {
            (island = new SkyIsland(this)).createIfNotExists();
        }
    }

    public void acceptInvite() {
        if (SkyBlockApi.getInstance().hasIsland(this)) {
            return;
        }

        invitationIsland.addMember(name);

        getBukkitHandle().sendMessage("§6§lOneBlock §8:: §fПоздравляем! Теперь Вы участник острова " + PlazmixUser.of(invitationIsland.getSkyPlayer().getName()).getDisplayName());

        Player islandOwner = invitationIsland.getSkyPlayer().getBukkitHandle();
        if (islandOwner != null) {

            islandOwner.sendMessage("§6§lOneBlock §8:: " + getHandle().getDisplayName() + " §fпринял Ваше приглашение на остров!");
        }

        invitationIsland = null;
        SkyBlockApi.getInstance().getPlugin().handleIslandInviteCallback(getBukkitHandle());
    }

    public void rejectInvite() {

        if (SkyBlockApi.getInstance().hasIsland(this)) {
            return;
        }

        getBukkitHandle().sendMessage("§6§lOneBlock §8:: §fПриглашение на остров от " + PlazmixUser.of(invitationIsland.getSkyPlayer().getName()).getDisplayName() + " §fбыло успешно отклонено!");

        Player islandOwner = invitationIsland.getSkyPlayer().getBukkitHandle();
        if (islandOwner != null) {

            islandOwner.sendMessage("§6§lOneBlock §8:: " + getHandle().getDisplayName() + " §fотклонил Ваше приглашение на остров!");
        }

        invitationIsland = null;
        SkyBlockApi.getInstance().getPlugin().handleIslandInviteCallback(getBukkitHandle());
    }

    public void save() {
        if (island != null) {
            island.saveIsland();
        }
    }


    public PlazmixUser getHandle() {
        String originalName = NetworkManager.INSTANCE.getPlayerName(NetworkManager.INSTANCE.getPlayerId(name));

        return PlazmixUser.of(originalName);
    }

    public Player getBukkitHandle() {
        return Bukkit.getPlayer(name);
    }

}
