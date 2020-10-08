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
import club.sk1er.hytilities.handlers.chat.adblock.AdBlocker;
import club.sk1er.hytilities.handlers.chat.cleaner.ChatCleaner;
import club.sk1er.hytilities.handlers.chat.connected.ConnectedMessage;
import club.sk1er.hytilities.handlers.chat.events.AchievementEvent;
import club.sk1er.hytilities.handlers.chat.events.LevelupEvent;
import club.sk1er.hytilities.handlers.chat.guild.GuildWelcomer;
import club.sk1er.hytilities.handlers.chat.restyler.ChatRestyler;
import club.sk1er.hytilities.handlers.chat.shoutblocker.ShoutBlocker;
import club.sk1er.hytilities.handlers.chat.swapper.AutoChatSwapper;
import club.sk1er.hytilities.handlers.chat.watchdog.ThankWatchdog;
import club.sk1er.hytilities.handlers.chat.whitechat.WhiteChat;
import club.sk1er.hytilities.handlers.chat.modules.blockers.*;
import club.sk1er.hytilities.handlers.chat.modules.events.*;
import club.sk1er.hytilities.handlers.chat.modules.modifiers.*;
import club.sk1er.hytilities.handlers.chat.modules.triggers.AutoChatSwapper;
import club.sk1er.mods.core.util.MinecraftUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

        this.registerModule(new AdBlocker());
        this.registerModule(new WhiteChat());
        this.registerModule(new ChatCleaner());
        this.registerModule(new QueueRestyler());
        this.registerModule(new LevelupEvent());
        this.registerModule(new AutoChatSwapper());
        this.registerModule(new AchievementEvent());
        this.registerModule(new ConnectedMessage());
        this.registerModule(customChatFormat = new CustomRestyleHandler());
        this.registerModule(new GameIsStartingInBlocker());

        this.moduleList.sort(Comparator.comparingInt(ChatModule::getPriority));
        this.registerReceiveModule(new AdBlocker());
        this.registerReceiveModule(new ChatCleaner());
        this.registerReceiveModule(new ChatRestyler());
        this.registerReceiveModule(new WhiteChat());
        this.registerReceiveModule(new LevelupEvent());
        this.registerReceiveModule(new AchievementEvent());
        this.registerReceiveModule(new AutoChatSwapper());
        this.registerReceiveModule(new ConnectedMessage());
        this.registerReceiveModule(new ThankWatchdog());
        this.registerReceiveModule(new GuildWelcomer());
        this.registerSendAndReceiveModule(new ShoutBlocker());

//        this.moduleList.sort(Comparator.comparingInt(ChatModule::getPriority));

        // reinitializing these seems to break them
        this.moduleList.add(0, Hytilities.INSTANCE.getLocrawUtil());
        this.moduleList.add(0, Hytilities.INSTANCE.getAutoQueue());
    }

    private void registerReceiveModule(ChatReceiveModule chatModule) {
        this.receiveModules.add(chatModule);
    }

    private void registerSendModule(ChatSendModule chatModule) {
        this.sendModules.add(chatModule);
        // inserted after the sort at the beginning so that they *always* run first, ignoring priority


    }

    private <T extends ChatSendModule & ChatReceiveModule> void registerSendAndReceiveModule(T chatModule) {
        this.registerReceiveModule(chatModule);
        this.registerSendModule(chatModule);
    }

    @SubscribeEvent
    public void handleChat(ClientChatReceivedEvent event) {
        if (!MinecraftUtils.isHypixel()) {
            return;
        }

        for (ChatReceiveModule module : this.receiveModules) {
            if (module.isReceiveModuleEnabled()) {
                module.onChatEvent(event);
                if (event.isCanceled()) {
                    return;
                }
            }
        }
    }

    public CustomRestyleHandler getCustomChatFormat() {
        return customChatFormat;
    }

    public boolean shouldSendMessage(String message) {
        if (!MinecraftUtils.isHypixel()) {
            return true;
        }

        for (ChatSendModule module : this.sendModules) {
            if (module.isSendModuleEnabled() && !module.shouldSendMessage(message)) return false;
        }
        return true;
    }
}
