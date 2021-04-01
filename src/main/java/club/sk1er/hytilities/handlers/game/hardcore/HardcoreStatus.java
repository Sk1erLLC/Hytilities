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

package club.sk1er.hytilities.handlers.game.hardcore;

import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.events.TitleEvent;
import club.sk1er.hytilities.tweaker.asm.GuiIngameForgeTransformer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.objectweb.asm.tree.ClassNode;

public class HardcoreStatus {

    @SubscribeEvent
    public void onTitle(TitleEvent event) {
        final String unformattedTitle = EnumChatFormatting.getTextWithoutFormattingCodes(event.getTitle());

        if (unformattedTitle != null && (unformattedTitle.equals("Your Mini Wither died!") ||
            unformattedTitle.equals("Your Wither died!") ||
            unformattedTitle.equals("BED DESTROYED!")) &&
            HytilitiesConfig.hardcoreHearts) {
            Minecraft.getMinecraft().theWorld.getWorldInfo().setHardcore(true);
        }
    }
}
