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

package club.sk1er.hytilities.asm.hooks;

import club.sk1er.hytilities.events.TitleEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
public class GuiIngameForgeHook {

    // Allow the title text to be hooked into.
    public static void postTitleEvent(String title, String subtitle) {
        TitleEvent event = new TitleEvent(title, subtitle);
        MinecraftForge.EVENT_BUS.post(event);

        // Set the title and subtitle to empty strings.
        if (event.isCanceled()) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(null, null, -1, -1, -1);
        }
    }
}
