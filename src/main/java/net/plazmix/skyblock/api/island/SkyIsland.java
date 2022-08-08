package net.plazmix.skyblock.api.island;

import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_12_R1.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import net.plazmix.configuration.BaseConfiguration;
import net.plazmix.coreconnector.core.network.NetworkManager;
import net.plazmix.skyblock.api.SkyBlockApi;
import net.plazmix.skyblock.api.island.upgrade.IslandUpgrade;
import net.plazmix.skyblock.api.player.SkyPlayer;
import net.plazmix.skyblock.api.util.IslandUtil;
import net.plazmix.utility.player.PlazmixUser;

import java.util.*;
import java.util.stream.Collectors;

public class SkyIsland extends BaseConfiguration {

    private final Map<String, Object> islandData        = new HashMap<>();

    @Getter
    private final SkyPlayer skyPlayer;


    public SkyIsland(@NonNull SkyPlayer skyPlayer) {
        super(SkyBlockApi.getInstance().getPlugin(), skyPlayer.getName().toLowerCase().concat(".yml"));

        this.skyPlayer = skyPlayer;
    }

    @Override
    protected void onInstall(@NonNull FileConfiguration fileConfiguration) {
        ConfigurationSection islandSection = fileConfiguration.getConfigurationSection("Island");
        if (islandSection == null) return;

        islandSection.getValues(false).forEach(this::addIslandData);
    }

    public void saveIsland() {
        getLoadedConfiguration().set("Island", null);

        islandData.forEach((dataKey, dataValue) -> getLoadedConfiguration().set("Island." + dataKey, dataValue));
        saveConfiguration();
    }


    public void addIslandData(@NonNull String key, Object value) {
        islandData.put(key.toLowerCase(), value);
    }

    public void addMember(@NonNull String playerName) {
        List<Integer> membersList = getMembers();

        membersList.add(NetworkManager.INSTANCE.getPlayerId(playerName));
        addIslandData("members", membersList);
    }

    public void removeMember(@NonNull String playerName) {
        if (getIslandData("members") == null)
            return;

        List<Integer> membersList = getIslandData("members");
        membersList.remove((Object) NetworkManager.INSTANCE.getPlayerId(playerName));

        addIslandData("members", membersList);
    }

    public List<Integer> getMembers() {
        return getIslandData("members", new LinkedList<>());
    }

    public int getMembersCount() {
        return getMembers().size();
    }

    public void removeIslandData(@NonNull String key) {
        islandData.remove(key.toLowerCase());
    }

    @SuppressWarnings("unchecked")
    public <T> T getIslandData(@NonNull String key) {
        return getIslandData(key, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getIslandData(@NonNull String key, T def) {
        return (T) islandData.getOrDefault(key.toLowerCase(), def);
    }

    public boolean isMember(@NonNull String playerName) {
        List<Integer> membersList = getIslandData("members");

        if (membersList == null) {
            return false;
        }

        return membersList.contains(NetworkManager.INSTANCE.getPlayerId(playerName));
    }

    public boolean hasIslandData(@NonNull String key) {
        return islandData.containsKey(key.toLowerCase());
    }

    public boolean isEmpty() {
        return islandData.isEmpty();
    }

    public void createBorder(int diameter) {

        Bukkit.getScheduler().runTaskLater(SkyBlockApi.getInstance().getPlugin(), () -> {
            Location center = IslandUtil.getIslandLocation(this);

            if (center == null || skyPlayer.getBukkitHandle() == null)
                return;

            WorldBorder worldBorder = new WorldBorder();

            worldBorder.setCenter(center.getBlockX(), center.getBlockZ());
            worldBorder.setSize(diameter);

            worldBorder.world = ((CraftWorld) center.getWorld()).getHandle();

            for (Player player : Bukkit.getOnlinePlayers().stream()
                    .filter(player -> Objects.equals(SkyBlockApi.getInstance().getIsland(player.getLocation()), this))
                    .collect(Collectors.toSet())) {

                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
                        new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE)
                );
            }

        }, 10);
    }

    public void createBorder(int diameter, Player receiver) {

        Bukkit.getScheduler().runTaskLater(SkyBlockApi.getInstance().getPlugin(), () -> {
            Location center = IslandUtil.getIslandLocation(this);

            if (center == null || skyPlayer.getBukkitHandle() == null)
                return;

            WorldBorder worldBorder = new WorldBorder();

            worldBorder.setCenter(center.getBlockX(), center.getBlockZ());
            worldBorder.setSize(diameter);

            worldBorder.world = ((CraftWorld) center.getWorld()).getHandle();
                ((CraftPlayer) receiver).getHandle().playerConnection.sendPacket(
                        new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE)
                );

        }, 10);
    }

    public int getUpgradeLevel(@NonNull IslandUpgrade islandUpgrade) {
        return getIslandData("upgrades_" + islandUpgrade.getClass().getSimpleName(), 0);
    }

    public void upgradeLevel(@NonNull IslandUpgrade islandUpgrade) {
        addIslandData("upgrades_" + islandUpgrade.getClass().getSimpleName(), getUpgradeLevel(islandUpgrade) + 1);
    }


    public void alertMessage(boolean sendToOwner, @NonNull String message) {

        for (int memberId : getMembers()) {
            Player memberPlayer = PlazmixUser.of(memberId).handle();

            if (memberPlayer != null) {
                memberPlayer.sendMessage(message);
            }
        }

        if (sendToOwner && skyPlayer.getBukkitHandle() != null) {
            skyPlayer.getBukkitHandle().sendMessage(message);
        }
    }

}
