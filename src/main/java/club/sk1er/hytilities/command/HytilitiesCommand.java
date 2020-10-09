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
import club.sk1er.hytilities.handlers.chat.customrestyle.CustomRestyle;
import club.sk1er.hytilities.handlers.chat.customrestyle.ReceivedMessageFormat;
import club.sk1er.mods.core.ModCore;
import com.google.gson.JsonParseException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static club.sk1er.hytilities.Hytilities.sendMessage;



public class HytilitiesCommand extends CommandBase {

    private final Map<String, ReceivedMessageFormat> formats = new HashMap<>();

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
        return "/" + getCommandName() + " [restyle] ...";
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            ModCore.getInstance().getGuiHandler().open(Hytilities.INSTANCE.getConfig().gui());
        } else {
            switch (args[0].toLowerCase(Locale.ENGLISH)) {
                case "restyle": {
                    if (args.length == 1) {
                        sendMessage("&cIncorrect arguments. Command usage is: /" + getCommandName() +
                            " restyle <set|remove|get|list> ...");
                    } else {
                        switch (args[1].toLowerCase(Locale.ENGLISH)) {
                            case "set": {
                                if (args.length < 4) {
                                    sendMessage("&cIncorrect arguments. Command usage is: /" +
                                        getCommandName() + " restyle set <name> <replacement>");
                                } else {
                                    if (formats.containsKey(args[2])) {
                                        for (CustomRestyle restyle : Hytilities.INSTANCE.getChatHandler()
                                            .getCustomChatFormat().getReplacements()) {
                                            if (restyle.getReceivedMessageFormat().getId().equals(args[2])) {
                                                Hytilities.INSTANCE.getChatHandler().getCustomChatFormat()
                                                    .getReplacements().remove(restyle);
                                                Hytilities.INSTANCE.getChatHandler().getCustomChatFormat()
                                                    .getReplacements().add(new CustomRestyle(formats.get(args[2]),
                                                    String.join(" ",
                                                        Arrays.copyOfRange(args, 3, args.length))));

                                                sendMessage("&aSet &r" + args[2] + "&a to &r" + String.join(
                                                    " ", Arrays.copyOfRange(args, 3, args.length)));
                                                Hytilities.INSTANCE.getChatHandler().getCustomChatFormat().save();
                                                return;
                                            }
                                        }

                                    } else {
                                        sendMessage("&cRestyle \"" + args[2] + "\" does not exist!");
                                    }
                                }
                                break;
                            }
                            case "remove": {
                                if (args.length != 3) {
                                    sendMessage("&cIncorrect arguments. Command usage is: /" +
                                        getCommandName() + " restyle remove <name>");
                                } else {
                                    if (formats.containsKey(args[2])) {
                                        for (CustomRestyle restyle : Hytilities.INSTANCE.getChatHandler()
                                            .getCustomChatFormat().getReplacements()) {
                                            if (restyle.getReceivedMessageFormat().getId().equals(args[2])) {
                                                Hytilities.INSTANCE.getChatHandler().getCustomChatFormat()
                                                    .getReplacements().remove(restyle);

                                                sendMessage("&aRemoved restyle for &r" + args[2] + "&a.");
                                                Hytilities.INSTANCE.getChatHandler().getCustomChatFormat().save();
                                                return;
                                            }
                                        }
                                    } else {
                                        sendMessage("&cRestyle \"&r" + args[2] + "&c\" does not exist!");
                                    }
                                }
                                break;
                            }
                            case "get": {
                                if (args.length != 3) {
                                    sendMessage("&cIncorrect arguments. Command usage is: /" +
                                        getCommandName() + " restyle get <name>");
                                } else {
                                    if (formats.containsKey(args[2])) {
                                        for (CustomRestyle restyle : Hytilities.INSTANCE.getChatHandler()
                                            .getCustomChatFormat().getReplacements()) {
                                            if (restyle.getReceivedMessageFormat().getId().equals(args[2])) {
                                                sendMessage(restyle.getReplacement());
                                                return;
                                            }
                                        }
                                        sendMessage("No value set for restyle \"" + args[2] + "\"!");
                                    } else {
                                        sendMessage("&cRestyle \"" + args[2] + "\" does not exist!");
                                    }
                                }
                                break;
                            }
                            case "list": {
                                if (args.length != 3) {
                                    sendMessage("&cIncorrect arguments. Command usage is: /" +
                                        getCommandName() + " list <all|modified>");
                                } else {
                                    switch (args[2].toLowerCase(Locale.ENGLISH)) {
                                        case "all": {
                                            sendMessage(String.join(", ", formats
                                                .keySet()
                                                .toArray(new String[0])));
                                            break;
                                        }
                                        case "modified": {
                                            CustomRestyle[] customRestyles = Hytilities.INSTANCE.getChatHandler()
                                                .getCustomChatFormat().getReplacements().toArray(new CustomRestyle[0]);
                                            List<String> restyleNames = new ArrayList<>();
                                            for (CustomRestyle restyle : customRestyles) {
                                                restyleNames.add(restyle.getReceivedMessageFormat().getId());
                                            }
                                            sendMessage(String.join(", ",
                                                restyleNames.toArray(new String[0])));
                                            break;
                                        }
                                        default: {
                                            sendMessage("&cIncorrect arguments. Command usage is: /" +
                                                getCommandName() + " list <all|modified>");
                                        }
                                    }
                                }
                                break;
                            }
                            case "reloadavailable": {
                                Hytilities.INSTANCE.getAvailableRestyles(true, false);
                                break;
                            }
                            case "reload": {
                                try {
                                    Hytilities.INSTANCE.getChatHandler().getCustomChatFormat().load();
                                    sendMessage("&aSuccessfully reloaded custom styles from file!");
                                } catch (IOException e) {
                                    Hytilities.INSTANCE.getLogger().error("Failed loading style file", e);
                                    sendMessage("&cFailed loading style file. Check logs for more info.");
                                } catch (JsonParseException e) {
                                    Hytilities.INSTANCE.getLogger().error(
                                        "Invalid JSON in style file. Did you modify it?", e);
                                    sendMessage("&cInvalid JSON in style file. Did you modify it?");
                                }
                                break;
                            }
                            case "info": {
                                if (args.length == 2) {
                                    sendMessage("Restyles version: " +
                                        Hytilities.INSTANCE.getRestyleMeta().get("version"));
                                    sendMessage("Restyles updated on: " +
                                        Hytilities.INSTANCE.getRestyleMeta().get("upload_date"));
                                    sendMessage("Restyles info: " +
                                        Hytilities.INSTANCE.getRestyleMeta().get("note"));
                                    sendMessage("Restyles type: " +
                                        Hytilities.INSTANCE.getRestyleMeta().get("type"));
                                    sendMessage(formats.size() + " available restyles.");
                                    break;
                                } else {
                                    if (formats.containsKey(args[2])) {
                                        ReceivedMessageFormat r = formats.get(args[2]);
                                        sendMessage("ReceivedMessageFormat " + args[2] + ":");
                                        sendMessage("    Regex: " + r.getRegex());
                                        if (r.getAvailableGroups().length != 0) {
                                            sendMessage("    Groups: " + String.join(", ",
                                                r.getAvailableGroups()));
                                        }
                                    } else {
                                        sendMessage("&cRestyle \"" + args[2] + "\" does not exist!");
                                    }
                                }
                                break;
                            }
                            default: {
                                sendMessage("&cIncorrect arguments. Command usage is: /" + getCommandName() +
                                    " restyle <set|remove|get|list|reloadavailable|reload|info> ...");
                            }
                        }
                    }
                    break;
                }
                default: {
                    sendMessage("&cIncorrect arguments. Command usage is: " + getCommandUsage(sender));
                }
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].toLowerCase(Locale.ENGLISH);
        }
        switch (args.length) {
            case 1: {
                return getListOfStringsMatchingLastWord(args, "restyle");
            }
            case 2: {
                if ("restyle".equals(args[0])) {
                    return getListOfStringsMatchingLastWord(args, "set", "get", "remove", "list",
                        "reloadavailable", "reload", "info");
                }
                break;
            }
            case 3: {
                switch (args[1]) {
                    case "list": {
                        return getListOfStringsMatchingLastWord(args, "all", "modified");
                    }
                    case "info":
                    case "set": {
                        return getListOfStringsMatchingLastWord(args, formats.keySet().toArray(new String[0]));
                    }
                    case "remove":
                    case "get": { // hope this isn't a big performance hit because this gets run a lot
                        List<String> list = new ArrayList<>();
                        for (CustomRestyle restyle : Hytilities.INSTANCE.getChatHandler().getCustomChatFormat()
                            .getReplacements()) {
                            list.add(restyle.getReceivedMessageFormat().getId());
                        }
                        return getListOfStringsMatchingLastWord(args, (String[]) list.toArray());
                    }
                }
            }
        }

        return null;
    }

    public void updateFormats(@NotNull Map<String, String> newMap) {
        formats.clear();
        for (String id : newMap.keySet()) {
            formats.put(id, new ReceivedMessageFormat(id, Pattern.compile(newMap.get(id)
                .replaceAll("\\\\{2}", "\\\\"))));
        }
    }

    public Map<String, ReceivedMessageFormat> getFormats() {
        return formats;
    }

}
