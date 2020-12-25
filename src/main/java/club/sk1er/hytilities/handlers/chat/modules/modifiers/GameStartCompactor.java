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

package club.sk1er.hytilities.handlers.chat.modules.modifiers;

import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatReceiveModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;

public class GameStartCompactor implements ChatReceiveModule {

    @Override
    public int getPriority() {
        return 7;
    }

    // Used to determine if the property has changed. TODO: Once ModCore 2 is out, have a listener for this.
    private boolean prevConfigValue = HytilitiesConfig.cleanerGameStartAnnouncements;
    private IChatComponent lastMessage = null;

    @Override
    public boolean onMessageReceived(@NotNull ClientChatReceivedEvent event) {
        final Matcher gameStartMatcher = getLanguage().chatRestylerGameStartCounterStyleRegex.matcher(event.message.getUnformattedText());
        final Matcher chatRestylerMatcher = getLanguage().chatRestylerGameStartCounterOutputStyleRegex.matcher(event.message.getFormattedText());
        if (gameStartMatcher.matches() || (HytilitiesConfig.gameStatusRestyle && chatRestylerMatcher.matches())) {
            if (lastMessage != null) {
                final GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                final List<IChatComponent> oldTimerLines = GuiUtilRenderComponents.splitText(lastMessage, MathHelper.floor_float((float) chat.getChatWidth() / chat.getChatScale()), Minecraft.getMinecraft().fontRendererObj, false, false);
                for (int i = chat.drawnChatLines.size() - 1; i >= 0; i--) { // tries to find the last message in drawn chat lines, if found, it removes them
                    final ChatLine drawnLine = chat.drawnChatLines.get(i);
                    for (IChatComponent oldTimerLine : oldTimerLines) {
                        if (drawnLine.getChatComponent().getFormattedText().equals(oldTimerLine.getFormattedText())) {
                            chat.drawnChatLines.remove(i);
                            oldTimerLines.remove(oldTimerLine);
                            break;
                        }
                    }
                }
            }

            lastMessage = event.message.createCopy();
        }
        return false;
    }

    @Override
    public boolean isEnabled() {
        // don't affect messages sent when the config value was false (or was true and was set to false later)
        if (!prevConfigValue && HytilitiesConfig.cleanerGameStartAnnouncements) {
            prevConfigValue = true;
            lastMessage = null;
        }

        return HytilitiesConfig.cleanerGameStartAnnouncements;
    }

}
