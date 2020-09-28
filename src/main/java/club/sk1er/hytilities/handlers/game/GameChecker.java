package club.sk1er.hytilities.handlers.game;

import club.sk1er.hytilities.Hytilities;

/**
 * Handles checking for the current location of the player using many different techniques.
 */

public class GameChecker {

    private GameType gameType = GameType.UNKNOWN;

    public GameType getGameType() {
        return Hytilities.INSTANCE.getLocrawUtil().getLocrawInformation().getGameType();
    }
}
