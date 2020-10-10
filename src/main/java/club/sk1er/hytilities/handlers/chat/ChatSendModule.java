package club.sk1er.hytilities.handlers.chat;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Since Forge lacks a {@link ClientChatReceivedEvent} for <em>sending</em> messages, this interface
 * is used to implement such an event. Register your class in {@link ChatHandler#ChatHandler()} and
 * it will be executed whenever the user sends a chat message.
 *
 * @see ChatModule
 * @see ChatHandler
 */
public interface ChatSendModule extends ChatModule {

    // Todo: Implement sent message modification
    /**
     * Place your code here. Called when the user sends a chat message.
     *
     * @param message message that the user sent
     * @return a {@code @Nullable} {@link String} - if {@code null}, then the sending will be cancelled
     */
    @Nullable
    String onMessageSend(@NotNull String message);

}
