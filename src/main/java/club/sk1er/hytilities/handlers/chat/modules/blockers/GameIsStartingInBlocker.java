package club.sk1er.hytilities.handlers.chat.modules.blockers;

import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatModule;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.util.regex.Pattern;

public class GameIsStartingInBlocker extends ChatModule {

    @Override
    public int getPriority() {
        return -2;
    }

    //                                                           it's always seconds, even with 1 second
    private final Pattern gameStartingInRegex = Pattern.compile("^The game is starting in \\d+ seconds!$");

    @Override
    public void onChatEvent(ClientChatReceivedEvent event) {
        if (gameStartingInRegex.matcher(event.message.getUnformattedText()).matches()) {
            event.setCanceled(true);
        }
    }

    @Override
    public boolean isEnabled() {
        return HytilitiesConfig.removeInitialGameStartingInMsg;
    }
}
