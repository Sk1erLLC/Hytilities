package club.sk1er.hytilities.tweaker.asm.hooks;

import club.sk1er.hytilities.config.HytilitiesConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;

public class GuiScreenHook {
    private static int clickCount = 0;

    public static boolean mouseClicked(GuiScreen screen) {
        if (!(screen instanceof GuiChest) || !HytilitiesConfig.doubleClickBedwars) return false;

        String name = ((GuiChest) screen).lowerChestInventory.getDisplayName().getUnformattedText();

        if (name.equals("Upgrades & Traps") || name.equals("Quick Buy")) {
            clickCount += 1;

            if (clickCount != 2) {
                return false;
            } else {
                clickCount = 0;
                return true;
            }
        }

        return false;
    }
}
