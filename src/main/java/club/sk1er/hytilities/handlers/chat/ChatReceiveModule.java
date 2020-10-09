package club.sk1er.hytilities.handlers.chat;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * This interface is used to handle many different {@link ClientChatReceivedEvent}-consuming methods
 * without having to add them all to the {@link MinecraftForge#EVENT_BUS}. ChatModules essentially behave
 * like small listener classes, except instead of going directly to Forge, the {@link ChatHandler}
 * handles them and passes them to Forge, taking account of things like priority and cancelled events.
 *
 * Must be registered in {@link ChatHandler#ChatHandler()} to be executed.
 *
 * @see ChatModule
 * @see ChatHandler
 */
public interface ChatReceiveModule extends ChatModule {

    /**
     * Place your code here. Called when a Forge {@link ClientChatReceivedEvent} is received.
     *
     * @param event a {@link ClientChatReceivedEvent}
     */
    void onMessageReceived(ClientChatReceivedEvent event);

}
