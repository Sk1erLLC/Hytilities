/*
 * Hytilities - Hypixel focused Quality of Life mod.
 * Copyright (C) 2020  Sk1er LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package club.sk1er.hytilities.handlers.chat.modules.blockers;

import club.sk1er.hytilities.Hytilities;
import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatModule;
import club.sk1er.hytilities.handlers.game.GameType;
import club.sk1er.hytilities.handlers.language.LanguageData;
import club.sk1er.hytilities.util.locraw.LocrawInformation;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.util.regex.Matcher;

/**
 * todo: split up this class into separate modules
 */
public class ChatCleaner extends ChatModule {

    @Override
    public int getPriority() {
        return -4;
    }

    private final List<String> joinMessageTypes = Arrays.asList(
        "joined the lobby", // normal
        "spooked in the lobby" // halloween
    );

    private final Pattern mysteryBoxFind = Pattern.compile("^(?<player>\\w{1,16}) found a \u2730{5} Mystery Box!$");
    private final Pattern soulWellFind = Pattern.compile("^.+ has found .+ in the Soul Well!$");
    private final Pattern gameAnnouncement = Pattern.compile("^\u27A4 A .+ game is (?:available to join|starting in .+ seconds)! CLICK HERE to join!$");
    private final Pattern bedwarsPartyAdvertisement = Pattern.compile("(?<number>[1-3]/[2-4])");
    private final Pattern connectionStatus = Pattern.compile("^(?:Friend|Guild) > (?<player>\\w{1,16}) (?:joined|left)\\.$");

    // Bad practice to include Unicode characters in Java files because it breaks compilation on Windows. (comments OK)
    // On IntelliJ, you can position the caret within the String, press ALT+ENTER and select "Check RegExp" and it will
    // show the RegEx with the escape sequences converted to their actual characters on the top line.
    private final Pattern mvpEmotes = Pattern.compile("\u00A7r\u00A7(?:c\u2764|6\u272e|a\u2714|c\u2716|b\u2615|e\u279c|e\u00af\\\\_\\(\u30c4\\)_/\u00af|c\\(\u256F\u00B0\u25A1\u00B0\uFF09\u256F\u00A7r\u00A7f\uFE35\u00A7r\u00A77 \u253B\u2501\u253B|d\\( \uFF9F\u25E1\uFF9F\\)/|a1\u00A7r\u00A7e2\u00A7r\u00A7c3|b\u2609\u00A7r\u00A7e_\u00A7r\u00A7b\u2609|e\u270E\u00A7r\u00A76\\.\\.\\.|a\u221A\u00A7r\u00A7e\u00A7l\\(\u00A7r\u00A7a\u03C0\u00A7r\u00A7a\u00A7l\\+x\u00A7r\u00A7e\u00A7l\\)\u00A7r\u00A7a\u00A7l=\u00A7r\u00A7c\u00A7lL|e@\u00A7r\u00A7a'\u00A7r\u00A7e-\u00A7r\u00A7a'|6\\(\u00A7r\u00A7a0\u00A7r\u00A76\\.\u00A7r\u00A7ao\u00A7r\u00A7c\\?\u00A7r\u00A76\\)|b\u0F3C\u3064\u25D5_\u25D5\u0F3D\u3064|e\\(\u00A7r\u00A7b'\u00A7r\u00A7e-\u00A7r\u00A7b'\u00A7r\u00A7e\\)\u2283\u00A7r\u00A7c\u2501\u00A7r\u00A7d\u2606\uFF9F\\.\\*\uFF65\uFF61\uFF9F|e\u2694|a\u270C|c\u00A7lOOF|e\u00A7l<\\('O'\\)>)\u00A7r");

    @Override
    public void onChatEvent(ClientChatReceivedEvent event) {
        String message = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getUnformattedText());

        if (HytilitiesConfig.lobbyStatus) {
            for (String messages : language.chatCleanerJoinMessageTypes) {
                if (message.contains(messages)) {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        if (HytilitiesConfig.mvpEmotes) {
            Matcher matcher = language.chatCleanerMvpEmotesRegex.matcher(event.message.getFormattedText());

            if (matcher.find(0)) {
                event.message = new ChatComponentText(event.message.getFormattedText().replaceAll(language.chatCleanerMvpEmotesRegex.pattern(), ""));
                return;
            }
        }

        if (HytilitiesConfig.lineBreakerTrim) {
            if ((message.contains("-----------") || message.contains("▬▬▬▬▬▬▬▬▬▬▬")) && !message.contains("\n")) {
                message = event.message.getFormattedText();
                int lineWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(message);
                int chatWidth = Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatWidth();
                if (lineWidth > chatWidth) {
                    message = Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(message, chatWidth);
                    event.message = new ChatComponentText(message);
                    return;
                }
            }
        }

        if (HytilitiesConfig.mysteryBoxAnnouncer) {
            Matcher matcher = language.chatCleanerMysteryBoxFindRegex.matcher(message);

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
            if (language.chatCleanerGameAnnouncementRegex.matcher(message).matches()) {
                event.setCanceled(true);
                return;
            }
        }

        if (HytilitiesConfig.hypeLimitReminder && message.startsWith(language.chatCleanerHypeLimit)) {
            event.setCanceled(true);
            return;
        }

        if (HytilitiesConfig.soulWellAnnouncer) {
            if (language.chatCleanerSoulWellFindRegex.matcher(message).matches()) {
                event.setCanceled(true);
                return;
            }
        }

        LocrawInformation locrawInformation = Hytilities.INSTANCE.getLocrawUtil().getLocrawInformation();
        if (locrawInformation != null) {
            if (HytilitiesConfig.bedwarsAdvertisements && locrawInformation.getGameType() == GameType.BED_WARS) {
                if (language.chatCleanerBedwarsPartyAdvertisementRegex.matcher(message).find()) {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        if (HytilitiesConfig.connectionStatus && language.chatCleanerConnectionStatusRegex.matcher(message).matches()) {
            event.setCanceled(true);
        }
    }

    @Override
    public boolean isEnabled() {
        return true; // always execute since there are a ton of different conditions
    }

    // taken from ToggleChat
    private String reformatMessage(String formattedText) {
        if (formattedText.contains("\u25AC\u25AC")) { // the character is "▬" - used in some separators
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
