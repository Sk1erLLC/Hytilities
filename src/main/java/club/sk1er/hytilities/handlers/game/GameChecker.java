package club.sk1er.hytilities.handlers.game;

import club.sk1er.mods.core.universal.ChatColor;
import club.sk1er.mods.core.util.Multithreading;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.TimeUnit;

/**
 * Handles checking for the current location of the player using many different techniques.
 *
 * @version 1.0
 */

public class GameChecker {

    private static GameType gameType = GameType.UNKNOWN;

    @SubscribeEvent
    public void worldLoad(WorldEvent.Load event) {
        // this will fire 3 times just bc of how the game works
        // may have a workaround, but for now this doesn't cause
        // any true issues and can be ignored.
        Multithreading.schedule(() -> {
            String scoreboardTitle = "";
            try {
                scoreboardTitle = ChatColor.stripColor(event.world.getScoreboard().getObjectiveInDisplaySlot(1).getDisplayName());
            } catch (Exception ignored) {
            }

            if (!scoreboardTitle.isEmpty()) {
                switch(scoreboardTitle) {
                    case "BED WARS":
                        gameType = GameType.BED_WARS;
                    case "SKYWARS":
                        gameType = GameType.SKY_WARS;
                }
            }
        }, 3, TimeUnit.SECONDS);
    }

    public static GameType getGameType() {
        return gameType;
    }
}
