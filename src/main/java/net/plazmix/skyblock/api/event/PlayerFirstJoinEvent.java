package net.plazmix.skyblock.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import net.plazmix.event.BaseCustomEvent;

@RequiredArgsConstructor
@Getter
public class PlayerFirstJoinEvent extends BaseCustomEvent {

    private final Player player;
}
