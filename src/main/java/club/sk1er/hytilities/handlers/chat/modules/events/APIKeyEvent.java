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

package club.sk1er.hytilities.handlers.chat.modules.events;

import club.sk1er.hytilities.Hytilities;
import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatReceiveModule;
import club.sk1er.hytilities.util.APIUtils;
import club.sk1er.mods.core.util.Multithreading;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class APIKeyEvent implements ChatReceiveModule {

    @Override
    public void onMessageReceived(@NotNull ClientChatReceivedEvent event) {
        /*/
            Adapted from Moulberry's NotEnoughUpdates, under the Attribution-NonCommercial 3.0 license.
            https://github.com/Moulberry/NotEnoughUpdates
         */
        final String unformattedText = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getUnformattedText());
        if (unformattedText.startsWith("Your new API key is ")) {
            String tempApiKey = unformattedText.substring("Your new API key is ".length());
            AtomicBoolean shouldReturn = new AtomicBoolean(false);
            Multithreading.runAsync(() -> {
                if (!APIUtils.getJSONResponse("https://api.hypixel.net/key?key=" + tempApiKey).get("success").getAsBoolean()) {
                    Hytilities.INSTANCE.sendMessage(EnumChatFormatting.RED + "The API Key was... invalid? Make sure you're running this command on Hypixel.");
                    shouldReturn.set(true);
                }
            });
            if (shouldReturn.get()) return;
            HytilitiesConfig.apiKey = unformattedText.substring("Your new API key is ".length());
            Hytilities.INSTANCE.getConfig().markDirty();
            Hytilities.INSTANCE.getConfig().writeData();
            Hytilities.INSTANCE.sendMessage(EnumChatFormatting.GREEN + "Your API Key has been automatically configured.");
        }
    }

    @Override
    public int getPriority() {
        return -3;
    }
}
