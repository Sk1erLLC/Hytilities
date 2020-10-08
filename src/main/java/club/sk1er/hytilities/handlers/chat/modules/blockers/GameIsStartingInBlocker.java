package club.sk1er.hytilities.handlers.chat.modules.blockers;

import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatReceiveModule;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class GameIsStartingInBlocker implements ChatReceiveModule {

    @Override
    public int getPriority() {
        return -2;
    }

    @Override
    public void onMessageReceived(ClientChatReceivedEvent event) {
        if (getLanguage().gameStartingInRegex.matcher(event.message.getUnformattedText()).matches()) {
            event.setCanceled(true);
        }
    }

    @Override
    public boolean isEnabled() {
        return HytilitiesConfig.removeInitialGameStartingInMsg;
    }
}
