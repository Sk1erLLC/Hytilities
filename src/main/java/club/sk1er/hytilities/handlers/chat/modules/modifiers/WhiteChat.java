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

import club.sk1er.hytilities.Hytilities;
import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatReceiveModule;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;

public class WhiteChat implements ChatReceiveModule {

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public void onMessageReceived(@NotNull ClientChatReceivedEvent event) {
        final String message = event.message.getFormattedText();
        final List<IChatComponent> siblings = event.message.getSiblings();
        boolean modified = false;

        if (HytilitiesConfig.whiteChat) {
            final Matcher matcher = getLanguage().whiteChatNonMessageRegex.matcher(message);
            if (matcher.find(0)) {
                event.message = new ChatComponentText(matcher.group("prefix") + ": " + matcher.group("message"));
                modified = true;
            }
        }

        if (!modified) return;
        Hytilities.INSTANCE.getChatHandler().fixStyling(event.message, siblings);
    }
}
