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
import club.sk1er.hytilities.handlers.chat.guild.GuildWelcomer;
import club.sk1er.hytilities.handlers.chat.modules.blockers.*;
import club.sk1er.hytilities.handlers.chat.modules.events.*;
import club.sk1er.hytilities.handlers.chat.modules.modifiers.*;
import club.sk1er.hytilities.handlers.chat.modules.triggers.*;
import club.sk1er.hytilities.tweaker.asm.GuiScreenTransformer;
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

    private final CustomRestyleHandler customChatFormat;

    public ChatHandler() {
        // Please sort by length increasing, in case of a tie use case-ignored alphabetical order
        // The actual sorting is done later I just want this to look nice

        this.registerReceiveModule(new AdBlocker());
        this.registerReceiveModule(new WhiteChat());
        this.registerReceiveModule(new ChatCleaner());
        this.registerReceiveModule(new LevelupEvent());
        this.registerReceiveModule(new GuildWelcomer());
        this.registerReceiveModule(new QueueRestyler());
        this.registerReceiveModule(new ThankWatchdog());
        this.registerReceiveModule(new AutoChatSwapper());
        this.registerReceiveModule(new AchievementEvent());
        this.registerReceiveModule(new ConnectedMessage());
        this.registerReceiveModule(new GameIsStartingInBlocker());
        this.registerReceiveModule(customChatFormat = new CustomRestyleHandler());

        this.registerSendAndReceiveModule(new ShoutBlocker());


        this.sendModules.sort(Comparator.comparingInt(ChatModule::getPriority));
    }

    private void registerReceiveModule(ChatReceiveModule chatModule) {
        this.receiveModules.add(chatModule);
    }

    private void registerSendModule(ChatSendModule chatModule) {
        this.sendModules.add(chatModule);
    }

    private <T extends ChatReceiveModule & ChatSendModule> void registerSendAndReceiveModule(T chatModule) {
        this.registerReceiveModule(chatModule);
        this.registerSendModule(chatModule);
    }

    @SubscribeEvent
    public void handleChat(ClientChatReceivedEvent event) {
        if (!MinecraftUtils.isHypixel()) {
            return;
        }

        // These don't cast to ChatSendModule for god knows why, so we can't include them in receiveModules.
        // Therefore, we manually trigger them here. Pain.
        Hytilities.INSTANCE.getLocrawUtil().onMessageReceived(event);
        Hytilities.INSTANCE.getAutoQueue().onMessageReceived(event);


        for (ChatReceiveModule module : this.receiveModules) {
            if (module.isEnabled()) {
                module.onMessageReceived(event);
                if (event.isCanceled()) {
                    return;
                }
            }
        }
    }

    /**
     * Allow modifying sent messages, or cancelling them altogether.
     *
     * Is not unused - is used in ASM ({@link GuiScreenTransformer}).
     *
     * @param message a message that the user has sent
     * @return the modified message, or {@code null} if the message should be cancelled
     */
    @SuppressWarnings({"unused", "RedundantSuppression"})
    @Nullable
    public String handleSentMessage(@NotNull String message) {
        if (!MinecraftUtils.isHypixel()) {
            return message;
        }

        for (ChatSendModule module : this.sendModules) {
            if (module.isEnabled()) {
                message = module.onMessageSend(message);
                if (message == null) {
                    return null;
                }
            }
        }
        return message;
    }

    public CustomRestyleHandler getCustomChatFormat() {
        return customChatFormat;
    }

}
