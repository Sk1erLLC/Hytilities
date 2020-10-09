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

package club.sk1er.hytilities.handlers.chat.guild;

import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatReceiveModule;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.util.regex.Matcher;

public class GuildWelcomer implements ChatReceiveModule {

    @Override
    public void onMessageReceived(ClientChatReceivedEvent event) {
        String text = event.message.getUnformattedText();

        Matcher matcher = getLanguage().guildPlayerJoinRegex.matcher(text);
        if (matcher.matches()) {
            String player = matcher.group("player");
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/gc Welcome to the guild " + player + "!");
        }
    }

    @Override
    public boolean isEnabled() {
        return HytilitiesConfig.guildWelcomer;
    }
}
