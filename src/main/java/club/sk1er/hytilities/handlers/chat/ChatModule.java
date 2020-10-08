package club.sk1er.hytilities.handlers.chat;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * This interface is used to handle many different {@link ClientChatReceivedEvent}s without
 * having to add them all to the {@link MinecraftForge#EVENT_BUS}. ChatModules essentially behave
 * like small listener classes, except instead of going directly to Forge, the {@link ChatHandler}
 * handles them and passes them to Forge, taking account of things like priority and cancelled
 * events.
 *
 * @author asbyth
 * @since 1.0b1
 */
public abstract class ChatModule {

    /**
     * This determines the order in which the modules are executed. The lower, the earlier.
     *
     * If your class removes messages then it is recommended to have a lower number.
     *
     * @return the class's priority
     */
    public int getPriority() {
        return 0;
    }

    /**
     * This is the actual code of the ChatModule. This function behaves similarly
     * to a {@code @SubscribeEvent} annotated function.
     *
     * @param event a {@link ClientChatReceivedEvent}
     */
    public abstract void onChatEvent(ClientChatReceivedEvent event);

    /**
     * This function allows you to determine if your ChatModule will be executed.
     * For example, one might return the a {@link club.sk1er.hytilities.config.HytilitiesConfig} value.
     *
     * @return a {@link boolean} that determines whether or not the code should be executed
     */
    public abstract boolean isEnabled();

}
