package club.sk1er.hytilities.handlers.chat;

import club.sk1er.hytilities.Hytilities;
import club.sk1er.hytilities.handlers.language.LanguageData;
import club.sk1er.mods.core.universal.ChatColor;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

/**
 * Used to have no conflicts for classes implementing both {@link ChatSendModule} and {@link ChatReceiveModule}
 */
public interface AbstractChatModule {
    default IChatComponent colorMessage(String message) {
        return new ChatComponentText(ChatColor.translateAlternateColorCodes('&', message));
    }

    default LanguageData getLanguage() {
        return Hytilities.INSTANCE.getLanguageHandler().getCurrent();
    }
}
