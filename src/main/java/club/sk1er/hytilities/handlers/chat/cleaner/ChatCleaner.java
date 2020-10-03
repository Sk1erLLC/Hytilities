package club.sk1er.hytilities.handlers.chat.cleaner;

import club.sk1er.hytilities.Hytilities;
import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatModule;
import club.sk1er.hytilities.handlers.game.GameType;
import club.sk1er.hytilities.util.locraw.LocrawInformation;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

/**
 * todo: split up this class into separate modules
 */
public class ChatCleaner implements ChatModule {

    private final List<String> joinMessageTypes = Arrays.asList(
            "joined the lobby", // normal
            "spooked in the lobby" // halloween
    );

    @Override
    public void onChatEvent(ClientChatReceivedEvent event) {
        if (event.isCanceled()) {
            return;
        }

        String message = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getUnformattedText());

        if (HytilitiesConfig.lobbyStatus) {
            for (String messages : Hytilities.INSTANCE.getLanguageHandler().getCurrent().chatCleanerJoinMessageTypes) {
                if (message.contains(messages)) {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        if (HytilitiesConfig.mvpEmotes) {
            Matcher matcher = Hytilities.INSTANCE.getLanguageHandler().getCurrent().chatCleanerMvpEmotesRegex.matcher(event.message.getFormattedText());

            if (matcher.find(0)) {
                event.message = new ChatComponentText(event.message.getFormattedText().replaceAll(Hytilities.INSTANCE.getLanguageHandler().getCurrent().chatCleanerMvpEmotesRegex.pattern(), ""));
                return;
            }
        }

        // todo: figure out why chat events don't copy-over
        /*if (HytilitiesConfig.hytilitiesLineBreaker) {
            if (message.contains("-----------") && message.contains("\n")) {
                event.message = new ChatComponentText(reformatMessage(event.message.getFormattedText()));
                return;
            } else if (message.contains("-----------")){
                event.setCanceled(true);
                return;
            }
        }*/

        if (HytilitiesConfig.mysteryBoxAnnouncer) {
            Matcher matcher = Hytilities.INSTANCE.getLanguageHandler().getCurrent().chatCleanerMysteryBoxFindRegex.matcher(message);

            if (matcher.matches()) {
                String player = matcher.group("player");
                boolean playerBox = !player.contains(Minecraft.getMinecraft().thePlayer.getName());

                if (!playerBox || !player.startsWith("You")) {
                    event.setCanceled(true);
                    return;
                }
            } else if (message.startsWith("[Mystery Box]")) {
                event.setCanceled(true);
                return;
            }
        }

        if (HytilitiesConfig.gameAnnouncements) {
            if (Hytilities.INSTANCE.getLanguageHandler().getCurrent().chatCleanerGameAnnouncementRegex.matcher(message).matches()) {
                event.setCanceled(true);
                return;
            }
        }

        if (HytilitiesConfig.hypeLimitReminder && message.startsWith(Hytilities.INSTANCE.getLanguageHandler().getCurrent().chatCleanerHypeLimit)) {
            event.setCanceled(true);
            return;
        }

        if (HytilitiesConfig.soulWellAnnouncer) {
            if (Hytilities.INSTANCE.getLanguageHandler().getCurrent().chatCleanerSoulWellFindRegex.matcher(message).matches()) {
                event.setCanceled(true);
                return;
            }
        }

        LocrawInformation locrawInformation = Hytilities.INSTANCE.getLocrawUtil().getLocrawInformation();
        if (locrawInformation != null) {
            if (HytilitiesConfig.bedwarsAdvertisements && locrawInformation.getGameType() == GameType.BED_WARS) {
                if (Hytilities.INSTANCE.getLanguageHandler().getCurrent().chatCleanerBedwarsPartyAdvertisementRegex.matcher(message).find()) {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        if (HytilitiesConfig.connectionStatus && Hytilities.INSTANCE.getLanguageHandler().getCurrent().chatCleanerConnectionStatusRegex.matcher(message).matches()) {
            event.setCanceled(true);
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // taken from ToggleChat
    private String reformatMessage(String formattedText) {
        if (formattedText.contains("\u25AC\u25AC")) { // the character is "▬" - used in some seperators
            formattedText = formattedText
                    .replace("\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC", "")
                    .replace("\u25AC\u25AC", "");
            return formattedText;
        } else if (formattedText.contains("---")) {
            formattedText = formattedText
                    .replace("----------------------------------------------------\n", "");
            return formattedText.replace("--\n", "").replace("\n--", "").replace("-", "");
        }

        return formattedText;
    }
}
