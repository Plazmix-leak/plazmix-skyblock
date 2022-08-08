package net.plazmix.skyblock.oneblock.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import net.plazmix.event.BaseCustomEvent;
import net.plazmix.skyblock.api.island.SkyIsland;

@RequiredArgsConstructor
@Getter
public class IslandLevelUpEvent extends BaseCustomEvent {

    private final Player player;

    private final SkyIsland island;

    private final int oldLevel;
    private final int newLevel;
}
