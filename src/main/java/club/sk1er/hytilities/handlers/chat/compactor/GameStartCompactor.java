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

package club.sk1er.hytilities.handlers.chat.compactor;

import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatReceiveModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.util.List;
import java.util.regex.Matcher;

public class GameStartCompactor implements ChatReceiveModule {
    private boolean prevConfigValue = HytilitiesConfig.cleanerGameStartAnnouncements; // Used to determine if the property has changed. TODO: Once ModCore 2 is out, have a listener for this.
    private IChatComponent lastMessage = null;

    @Override
    public void onChatEvent(ClientChatReceivedEvent event) {
        Matcher gameStartMatcher = getLanguage().chatRestylerGameStartCounterStyleRegex.matcher(event.message.getUnformattedText());
        Matcher chatRestylerMatcher = getLanguage().chatRestylerGameStartCounterOutputStyleRegex.matcher(event.message.getFormattedText());
        if (!event.isCanceled() && (gameStartMatcher.matches() || (HytilitiesConfig.gameStatusRestyle && chatRestylerMatcher.matches()))) {
            if (lastMessage != null) {
                GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                List<IChatComponent> oldTimerLines = GuiUtilRenderComponents.splitText(lastMessage, MathHelper.floor_float((float) chat.getChatWidth() / chat.getChatScale()), Minecraft.getMinecraft().fontRendererObj, false, false);
                for (int i = chat.drawnChatLines.size() - 1; i >= 0; i--) { // tries to find the last message in drawn chat lines, if found, it removes them
                    ChatLine drawnLine = chat.drawnChatLines.get(i);
                    for (IChatComponent oldTimerLine : oldTimerLines) {
                        if (drawnLine.getChatComponent().getFormattedText().equals(oldTimerLine.getFormattedText())) {
                            chat.drawnChatLines.remove(i);
                            oldTimerLines.remove(oldTimerLine);
                            break;
                        }
                    }
                }
                if (oldTimerLines.size() > 0) { // just prints an error, can be removed if no issues are found
                    StringBuilder leftLines = new StringBuilder("Could not remove all lines from last game start message. Ignore this error if the chat was recently cleared. Lines, that couldn't be found: ");
                    for (IChatComponent oldTimerLine : oldTimerLines) leftLines.append(oldTimerLine.getUnformattedText()).append(", ");
                    System.out.println(leftLines.substring(0, leftLines.toString().length()-2));
                }
            }
            lastMessage = event.message.createCopy();
        }
    }

    @Override
    public boolean isReceiveModuleEnabled() {
        if (!prevConfigValue && HytilitiesConfig.cleanerGameStartAnnouncements) { // don't affect messages sent when the config value was false (or was true and was set to false later)
            prevConfigValue = true;
            lastMessage = null;
        }
        return HytilitiesConfig.cleanerGameStartAnnouncements;
    }
}
