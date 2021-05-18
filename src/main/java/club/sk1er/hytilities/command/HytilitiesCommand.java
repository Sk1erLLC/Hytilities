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

package club.sk1er.hytilities.command;

import club.sk1er.hytilities.Hytilities;
import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.util.APIUtils;
import club.sk1er.hytilities.util.guild.GexpUtils;
import club.sk1er.mods.core.ModCore;
import club.sk1er.mods.core.gui.notification.Notifications;
import club.sk1er.mods.core.util.Multithreading;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collections;
import java.util.List;

public class HytilitiesCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "hytilities";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("hytils");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName() + " [setkey|gexp|info]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length <= 0) {
            ModCore.getInstance().getGuiHandler().open(Hytilities.INSTANCE.getConfig().gui());
        } else {
            switch (args[0].toLowerCase()) {
                case "setkey": {
                    Multithreading.runAsync(() -> {
                        if (args.length == 1 & APIUtils.getJSONResponse("https://api.hypixel.net/key?key=" + args[1]).get("success").getAsBoolean()) {
                            HytilitiesConfig.apiKey = args[1];
                            Hytilities.INSTANCE.getConfig().markDirty();
                            Hytilities.INSTANCE.getConfig().writeData();
                            Hytilities.INSTANCE.sendMessage(EnumChatFormatting.GREEN + "Successfully saved the API key.");
                        } else {
                            Hytilities.INSTANCE.sendMessage(EnumChatFormatting.RED + "Invalid API key.");
                        }
                    });
                    break;
                }
                case "gexp": {
                    if (HytilitiesConfig.apiKey.equals("")) {
                        Hytilities.INSTANCE.sendMessage(EnumChatFormatting.RED + "You need to provide a valid API key to run this command! Type /api new to autoset a key.");
                    } else {

                        if (args.length <= 1) {
                            Notifications.INSTANCE.pushNotification("Hytilities", "You currently have " + GexpUtils.instance.getGEXP() + " guild EXP.");
                        } else {
                            Notifications.INSTANCE.pushNotification("Hytilities", args[1] + " currently has " + GexpUtils.instance.getGEXP(args[1]) + " guild EXP.");
                        }
                    }
                    break;
                }
                case "info": {
                    Hytilities.INSTANCE.sendMessage("\nHytilities is a Hypixel focused Forge 1.8.9 mod, adding tons of Quality of Life features that you would want while on Hypixel, such as a player-advertisement blocker, an NPC hider, etc.");
                }
            }

        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
