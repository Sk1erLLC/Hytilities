package club.sk1er.hytilities.handlers.game.bedwars;

import club.sk1er.hytilities.tweaker.asm.hooks.GuiScreenHook;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DoubleClickBedwarsHandler {
    @SubscribeEvent
    public void onGuiClose(GuiOpenEvent event) {
        if (event.gui == null) {
            GuiScreenHook.clickCount = 0;
        }
    }
}
