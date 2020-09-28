package club.sk1er.hytilities.util.locraw;

import club.sk1er.hytilities.handlers.game.GameType;
import com.google.gson.annotations.SerializedName;

public class LocrawInformation {

    @SerializedName("server")
    private String serverId = "mini000X";
    @SerializedName("mode")
    private String gameMode = "unknown";
    @SerializedName("map")
    private String mapName = "None";

    @SerializedName("gametype")
    private String gameTypeRaw;
    private GameType gameType = GameType.UNKNOWN;

    public String getGameTypeRaw() {
        return gameTypeRaw;
    }

    public String getServerId() {
        return serverId;
    }

    public String getGameMode() {
        return gameMode;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public String getMapName() {
        return mapName;
    }
}