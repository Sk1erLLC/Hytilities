package club.sk1er.hytilities.handlers.chat.whitechat;

import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatReceiveModule;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.util.regex.Matcher;

public class WhiteChat implements ChatReceiveModule {

    @Override
    public void onChatEvent(ClientChatReceivedEvent event) {
        if (HytilitiesConfig.whitePrivateMessages) {
            Matcher matcher = getLanguage().privateMessageWhiteChatRegex.matcher(event.message.getFormattedText());

            if (matcher.find(0)) {
                event.message = new ChatComponentText(matcher.group("type") + " " + matcher.group("prefix") + ": " + matcher.group("message").replace("§7", "§f"));
            }

            return;
        }

        if (HytilitiesConfig.whiteChat) {
            Matcher matcher = getLanguage().whiteChatNonMessageRegex.matcher(event.message.getFormattedText());

            if (matcher.find(0)) {
                event.message = new ChatComponentText(matcher.group("prefix") + ": " + matcher.group("message"));
            }

        }
    }

    @Override
    public boolean isReceiveModuleEnabled() {
        return true;
    }
}
