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

package club.sk1er.hytilities.handlers.chat.modules.blockers;

import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatReceiveModule;
import club.sk1er.hytilities.handlers.language.LanguageData;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

public class MysteryBoxAnnouncer implements ChatReceiveModule {
    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public void onMessageReceived(@NotNull ClientChatReceivedEvent event) {
        final LanguageData language = getLanguage();
        String message = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getUnformattedText());
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

    @Override
    public boolean isEnabled() {
        return HytilitiesConfig.mysteryBoxAnnouncer;
    }
}
