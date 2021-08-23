/*
 * Hytilities - Hypixel focused Quality of Life mod.
 * Copyright (C) 2021  Sk1er LLC
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

package club.sk1er.hytilities.handlers.chat.modules.modifiers;

import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatReceiveModule;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrimLineBreakers implements ChatReceiveModule {

    /**
     * Hypixel uses two different line-breakers for some reason.
     * Top one for info in game-modes and the bottom one for f.ex. the help menu for /party
     * §r§a§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬§r
     * §9§m---------------------§r
     * This regex also grabs the bold color code so that it can resize it properly.
     */
    private final Pattern lineBreakerPattern = Pattern.compile("\\b(§l)?\\b([-▬])+");
    private final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void onMessageReceived(@NotNull ClientChatReceivedEvent event) {
        String message = event.message.getFormattedText();
        Matcher regex = lineBreakerPattern.matcher(message);
        if (regex.find()) {
            ChatComponentText trimmedComponent = new ChatComponentText(this.trimLineBreaker(message));
            trimmedComponent.setChatStyle(event.message.getChatStyle().createShallowCopy());

            for (IChatComponent sibling : event.message.getSiblings()) {
                ChatComponentText trimmedSibling = new ChatComponentText(this.trimLineBreaker(sibling.getFormattedText()));
                trimmedSibling.setChatStyle(sibling.getChatStyle().createShallowCopy());
                trimmedComponent.appendSibling(trimmedSibling);
            }

            event.message = trimmedComponent;
        }
    }

    @Override
    public boolean isEnabled() {
        return HytilitiesConfig.lineBreakerTrim;
    }

    @Override
    public int getPriority() {
        return -1;
    }

    private String trimLineBreaker(String text) {
        Matcher regex = lineBreakerPattern.matcher(text);
        if (regex.find()) {
            String linebreak = regex.group();
            int chatWidth = mc.ingameGUI.getChatGUI().getChatWidth();
            String newLineBreaker = mc.fontRendererObj.trimStringToWidth(linebreak, chatWidth);
            text = regex.replaceAll(newLineBreaker);
        }
        return text;
    }
}
