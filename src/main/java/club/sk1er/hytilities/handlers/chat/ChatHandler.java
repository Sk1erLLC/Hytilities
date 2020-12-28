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

package club.sk1er.hytilities.handlers.chat;

import club.sk1er.hytilities.Hytilities;
import club.sk1er.hytilities.handlers.chat.modules.blockers.AdBlocker;
import club.sk1er.hytilities.handlers.chat.modules.blockers.ChatCleaner;
import club.sk1er.hytilities.handlers.chat.modules.blockers.ConnectedMessage;
import club.sk1er.hytilities.handlers.chat.modules.blockers.GuildMOTD;
import club.sk1er.hytilities.handlers.chat.modules.blockers.ShoutBlocker;
import club.sk1er.hytilities.handlers.chat.modules.events.AchievementEvent;
import club.sk1er.hytilities.handlers.chat.modules.events.LevelupEvent;
import club.sk1er.hytilities.handlers.chat.modules.modifiers.DefaultChatRestyler;
import club.sk1er.hytilities.handlers.chat.modules.modifiers.GameStartCompactor;
import club.sk1er.hytilities.handlers.chat.modules.modifiers.LimboPlayCommandHelper;
import club.sk1er.hytilities.handlers.chat.modules.modifiers.WhiteChat;
import club.sk1er.hytilities.handlers.chat.modules.triggers.AutoChatSwapper;
import club.sk1er.hytilities.handlers.chat.modules.triggers.AutoQueue;
import club.sk1er.hytilities.handlers.chat.modules.triggers.ChatReportConfirmer;
import club.sk1er.hytilities.handlers.chat.modules.triggers.GuildWelcomer;
import club.sk1er.hytilities.handlers.chat.modules.triggers.ThankWatchdog;
import club.sk1er.hytilities.tweaker.asm.EntityPlayerSPTransformer;
import club.sk1er.hytilities.util.locraw.LocrawUtil;
import club.sk1er.mods.core.util.MinecraftUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ChatHandler {

    private final List<ChatModule> modules;

    /**
     * Please order the modules in size-increasing order, and in
     * case of a tie, use alphabetical order.
     *
     * @param specialModules because {@link Hytilities#INSTANCE} isn't available at Hytilities clinit.
     */
    public ChatHandler(final ChatModule... specialModules) {
        modules = Arrays.asList(ArrayUtils.addAll(specialModules,
            new AdBlocker(),
            new GuildMOTD(),
            new WhiteChat(),
            new ChatCleaner(),
            new LevelupEvent(),
            new ShoutBlocker(),
            new GuildWelcomer(),
            new ThankWatchdog(),
            new AutoChatSwapper(),
            new AchievementEvent(),
            new ConnectedMessage(),
            new GameStartCompactor(),
            new ChatReportConfirmer(),
            new DefaultChatRestyler(),
            new LimboPlayCommandHelper()
        ));

        modules.sort(Comparator.comparingInt(ChatModule::getPriority));
    }


    @SubscribeEvent
    public void handleChat(final ClientChatReceivedEvent event) {
        if (MinecraftUtils.isHypixel()) {
            for (final ChatModule module : this.modules) {
                if (module.isEnabled() && module instanceof ChatReceiveModule &&
                    ((ChatReceiveModule) module).onMessageReceived(event)) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    /**
     * Allow modifying sent messages, or cancelling them altogether.
     * <p>
     * Used in {@link EntityPlayerSPTransformer}.
     *
     * @param message a message that the user has sent
     * @return the modified message, or {@code null} if the message should be cancelled
     */
    @SuppressWarnings({"unused", "RedundantSuppression"})
    @Nullable
    public String handleSentMessage(@NotNull String message) {
        if (MinecraftUtils.isHypixel()) {
            for (final ChatModule module : this.modules) {
                if (module.isEnabled() && module instanceof ChatSendModule) {
                    message = ((ChatSendModule) module).onMessageSend(message);
                    if (message == null) return null;
                }
            }
        }

        return message;
    }
}
