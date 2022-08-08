package net.plazmix.skyblock.oneblock.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import net.plazmix.event.BaseCustomEvent;

@RequiredArgsConstructor
@Getter
public class TutorialEndEvent extends BaseCustomEvent {

    private final Player player;
}
