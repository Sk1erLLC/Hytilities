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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChatHandler {

    private final List<ChatReceiveModule> receiveModules = new ArrayList<>();
    private final List<ChatSendModule> sendModules = new ArrayList<>();

    /**
     * Please order the modules in size-increasing order, and in
     * case of a tie, use alphabetical order.
     */
    public ChatHandler() {
        registerModules(
            new AdBlocker(),
            new AutoQueue(),
            new GuildMOTD(),
            new WhiteChat(),
            new LocrawUtil(),
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
        );

        sendModules.sort(Comparator.comparingInt(ChatModule::getPriority));
        receiveModules.sort(Comparator.comparingInt(ChatModule::getPriority));
    }

    private void registerModules(final ChatModule... modules) {
        for (final ChatModule module: modules) {
            if (module instanceof ChatSendModule) {
                this.sendModules.add((ChatSendModule) module);
            }
            if (module instanceof ChatReceiveModule) {
                this.receiveModules.add((ChatReceiveModule) module);
            }
        }
    }

    @SubscribeEvent
    public void handleChat(ClientChatReceivedEvent event) {
        if (MinecraftUtils.isHypixel()) {

            // These don't cast to ChatReceiveModule for god knows why, so we can't include them in receiveModules.
            // Therefore, we manually trigger them here.
//            Hytilities.INSTANCE.getLocrawUtil().onMessageReceived(event);
//            Hytilities.INSTANCE.getAutoQueue().onMessageReceived(event);

            for (ChatReceiveModule module : this.receiveModules) {
                if (module.isEnabled()) {
                    if (module.onMessageReceived(event)) {
                        event.setCanceled(true);
                        return;
                    }
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
//            Hytilities.INSTANCE.getLocrawUtil().onMessageSend(message);

            for (ChatSendModule module : this.sendModules) {
                if (module.isEnabled()) {
                    message = module.onMessageSend(message);
                    if (message == null) {
                        return null;
                    }
                }
            }
        }
        return message;
    }
}
