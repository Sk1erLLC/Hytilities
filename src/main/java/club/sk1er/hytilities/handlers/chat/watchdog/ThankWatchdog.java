package club.sk1er.hytilities.handlers.chat.watchdog;

import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatModule;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class ThankWatchdog implements ChatModule {
    private static final String WATCHDOG_BAN_TRIGGER = "A player has been removed from your game for hacking or abuse. Thanks for reporting it!";
    private static final String WATCHDOG_ANNOUNCEMENT_TRIGGER = "[WATCHDOG ANNOUNCEMENT]";
    private static final String THANK_WATCHDOG_MESSAGE = "/achat Thanks Watchdog!";

    @Override
    public void onChatEvent(ClientChatReceivedEvent event) {
        String text = event.message.getUnformattedText();

        if (text.contains(WATCHDOG_BAN_TRIGGER) || text.contains(WATCHDOG_ANNOUNCEMENT_TRIGGER)) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage(THANK_WATCHDOG_MESSAGE);
        }
    }

    @Override
    public boolean isEnabled() {
        return HytilitiesConfig.thankWatchdog;
    }
}
