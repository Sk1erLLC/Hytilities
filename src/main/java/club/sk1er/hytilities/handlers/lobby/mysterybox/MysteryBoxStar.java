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

package club.sk1er.hytilities.handlers.lobby.mysterybox;

import club.sk1er.hytilities.Hytilities;
import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.mods.core.util.MinecraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MysteryBoxStar {

    private Pattern mysteryBoxStarPattern = Pattern.compile("^\\u00a75\\u00a7o\\u00a77\\u00a77Quality: \\u00a7e(?<stars>\\u2730{1,5}).*");

    @SubscribeEvent
    public void onDrawScreenPre(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (!HytilitiesConfig.mysteryBoxStar || !MinecraftUtils.isHypixel() || !Hytilities.INSTANCE.getLobbyChecker().playerIsInLobby()) {
            return;
        }
        if (event.gui instanceof GuiChest) {
            GuiChest guiChest = (GuiChest) event.gui;
            Container inventorySlots = guiChest.inventorySlots;
            IInventory inventory = inventorySlots.getSlot(0).inventory;
            if (inventory.getName().equals("Mystery Vault")) {
                int guiLeft = (guiChest.width - 176) / 2;
                int inventoryRows = inventory.getSizeInventory() / 9;
                int ySize = 222 - 108 + inventoryRows * 18;
                int guiTop = (guiChest.height - ySize) / 2;
                float scaleFactor = 0.9f;
                GlStateManager.pushMatrix();
                GlStateManager.translate(0, 0, 260);
                GlStateManager.scale(scaleFactor, scaleFactor, 0);
                for (Slot inventorySlot : inventorySlots.inventorySlots) {
                    int slotRow = inventorySlot.slotNumber / 9;
                    int maxRow = inventoryRows - 1;
                    if (slotRow < maxRow && inventorySlot.getHasStack()) {
                        int slotX = (int) ((guiLeft + inventorySlot.xDisplayPosition) / scaleFactor) + 2;
                        int slotY = (int) ((guiTop + inventorySlot.yDisplayPosition) / scaleFactor) + 1;
                        drawStars(inventorySlot.getStack(), slotX, slotY);
                    }
                }
                GlStateManager.popMatrix();
            }
        }
    }

    public void drawStars(ItemStack item, int x, int y) {
        // avoid rendering star when no mystery boxes are present
        if (item.getItem() == Item.getItemFromBlock(Blocks.stained_glass_pane)) {
            return;
        }
        // avoid rendering star on bags of experience
        if (item.getItem() == Items.dye) {
            return;
        }
        List<String> tooltip = item.getTooltip(Minecraft.getMinecraft().thePlayer, false);
        // avoid accessing negative index
        if (tooltip.size() < 4) {
            return;
        }
        String line = tooltip.get(tooltip.size() - 4);
        Matcher matcher = mysteryBoxStarPattern.matcher(line);
        if (matcher.matches()) {
            int stars = matcher.group("stars").length();
            // yellow stars for regular boxes
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(stars + "✰", x, y, -14080);
        } else {
            // not a regular box, so assume it is a special box. e.g. holiday boxes
            // orange stars for special boxes
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("✰", x, y, -34304);
        }
    }
}
