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
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuickWDRCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "quickwatchdogreport";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("qwdr");
    }

    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return "/" + getCommandName() + " <username> <arksdf>";
    }


    @Override
    public void processCommand(final ICommandSender sender, final String[] args) {
        if (args.length == 0 || args.length == 1) {
            Hytilities.sendMessage("&cYou must provide a playername and a report type!");
        } else {
            if (args.length > 2) {
                args[1] = String.join("", Arrays.copyOfRange(args, 1, args.length));
                // we can now disregard the rest of the array
            }
            final StringBuilder builder = new StringBuilder("/wdr " + args[0]);
            if (args[1].contains("a")) {
                builder.append(" autoclicker");
            }
            if (args[1].contains("r")) {
                builder.append(" reach");
            }
            if (args[1].contains("k")) {
                builder.append(" killaura");
            }
            if (args[1].contains("s")) {
                builder.append(" speed");
            }
            if (args[1].contains("d")) {
                builder.append(" dolphin");
            }
            if (args[1].contains("f")) {
                builder.append(" fly");
            }
            Hytilities.INSTANCE.getCommandQueue().queue(builder.toString());
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
