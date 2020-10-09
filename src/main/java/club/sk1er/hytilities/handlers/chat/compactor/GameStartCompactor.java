package club.sk1er.hytilities.handlers.chat.compactor;

import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatReceiveModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.util.List;
import java.util.regex.Matcher;

public class GameStartCompactor implements ChatReceiveModule {
    private boolean prevConfigValue = HytilitiesConfig.cleanerGameStartAnnouncements; // Used to determine if the property has changed. TODO: Once ModCore 2 is out, have a listener for this.
    private String lastMessageTimer = null;
    private String lastMessageUnit = null;

    @Override
    public void onChatEvent(ClientChatReceivedEvent event) {
        Matcher gameStartMatcher = getLanguage().chatRestylerGameStartCounterStyleRegex.matcher(event.message.getUnformattedText());
        Matcher chatRestylerMatcher = getLanguage().chatRestylerGameStartCounterOutputStyleRegex.matcher(event.message.getFormattedText());
        if (!event.isCanceled() && (gameStartMatcher.matches() || (HytilitiesConfig.gameStatusRestyle && chatRestylerMatcher.matches()))) {
            String time = (gameStartMatcher.matches() ? gameStartMatcher : chatRestylerMatcher).group("time");
            String unit = (gameStartMatcher.matches() ? gameStartMatcher : chatRestylerMatcher).group("unit");
            if (lastMessageTimer != null && lastMessageUnit != null) {
                GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                IChatComponent copy = event.message.createCopy();

                for (int i = -1; i < copy.getSiblings().size(); i++) { // creates a chat component of what it expects the last message to be based on lastMessageTimer
                    IChatComponent siblingOfCopy = i == -1 ? copy : copy.getSiblings().get(i);
                    if (siblingOfCopy instanceof ChatComponentText) {
                        IChatComponent replacement = new ChatComponentText(siblingOfCopy.getUnformattedTextForChat().replace(time, lastMessageTimer).replace(unit, lastMessageUnit));
                        if (i != -1) copy.getSiblings().set(i, replacement);
                        else copy = replacement.setChatStyle(copy.getChatStyle());
                    }
                }

                List<IChatComponent> oldTimerLines = GuiUtilRenderComponents.splitText(copy, MathHelper.floor_float((float) chat.getChatWidth() / chat.getChatScale()), Minecraft.getMinecraft().fontRendererObj, false, false);
                for (int i = chat.drawnChatLines.size() - 1; i >= 0; i--) { // tries to find the copy in drawn chat lines, if found, it removes them
                    ChatLine drawnLine = chat.drawnChatLines.get(i);
                    for (IChatComponent oldTimerLine : oldTimerLines) {
                        if (drawnLine.getChatComponent().getFormattedText().equals(oldTimerLine.getFormattedText())) {
                            chat.drawnChatLines.remove(i);
                            oldTimerLines.remove(oldTimerLine);
                            break;
                        }
                    }
                }
                if (oldTimerLines.size() > 0) { // just prints an error, can be removed if no issues are found
                    StringBuilder leftLines = new StringBuilder("Could not remove all lines from last game start message. Ignore this error if the chat was recently cleared. Lines, that couldn't be found: ");
                    for (IChatComponent oldTimerLine : oldTimerLines) leftLines.append(oldTimerLine.getUnformattedText()).append(", ");
                    System.out.println(leftLines.substring(0, leftLines.toString().length()-2));
                }
            }
            lastMessageTimer = time;
            lastMessageUnit = unit;
        }
    }

    @Override
    public boolean isReceiveModuleEnabled() {
        if (!prevConfigValue && HytilitiesConfig.cleanerGameStartAnnouncements) { // don't affect messages sent when the config value was false (or was true and was set to false later)
            prevConfigValue = true;
            lastMessageTimer = null;
            lastMessageUnit = null;
        }
        return HytilitiesConfig.cleanerGameStartAnnouncements;
    }
}
