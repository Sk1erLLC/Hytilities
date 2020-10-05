package club.sk1er.hytilities.handlers.chat.connected;

import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatReceiveModule;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class ConnectedMessage implements ChatReceiveModule {
    @Override
    public void onChatEvent(ClientChatReceivedEvent event) {
        if (getLanguage().connectedServerConnectMessageRegex.matcher(event.message.getUnformattedText()).matches()) {
            event.setCanceled(true);
        }
    }

    @Override
    public boolean isReceiveModuleEnabled() {
        return HytilitiesConfig.serverConnectedMessages;
    }
}
