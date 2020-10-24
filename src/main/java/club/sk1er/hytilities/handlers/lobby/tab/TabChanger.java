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

package club.sk1er.hytilities.handlers.lobby.tab;

import club.sk1er.hytilities.Hytilities;
import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.game.GameType;
import club.sk1er.hytilities.tweaker.asm.GuiPlayerTabOverlayTransformer;
import club.sk1er.hytilities.util.locraw.LocrawInformation;
import club.sk1er.mods.core.util.JsonHolder;
import club.sk1er.mods.core.util.MinecraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Used in {@link GuiPlayerTabOverlayTransformer#transform(ClassNode, String)}
 */
@SuppressWarnings("unused")
public class TabChanger {
    public static String modifyName(String name) {
        if (MinecraftUtils.isHypixel()) {
            if (HytilitiesConfig.hidePlayerRanksInTab && name.startsWith("[", 2)) {
                // keep the name color if player rank is removed
                // §b[MVP§c+§b] Steve
                String color = "\u00a7" + name.charAt(1);

                // add the rank color, and trim off the player rank
                name = color + name.substring(name.indexOf("]") + 2);
            }

            if (HytilitiesConfig.hideGuildTagsInTab && name.endsWith("]")) {
                // trim off the guild tag
                // e.g. Steve §6[GUILD]
                name = name.substring(0, name.lastIndexOf("[") - 3);
            }

            if (HytilitiesConfig.boldFriendNamesInTab) {
                // If a list of friends has already been generated (i.e. friends != null),
                // modify the player's name if they are a friend
                if (friends != null) {
                    // Divides the display name up into parts based on the spaces
                    String[] segments = name.split(" ");

                    // If the first term does not contains "[", then return that term without the color code
                    // If the first term contains "[", then return the second term
                    // For example:
                    // §7Steve, §6[GUILD] -> §7Steve -> Steve (First term does not contain "[")
                    // §b[MVP§c+§b], Steve -> Steve (First term contains "[")
                    assert segments[0].length() > 2; // First segment should be more than 2 letters long because of color codes
                    String username = !segments[0].contains("[") ? segments[0].substring(2) : segments[1];

                    // If user is a friend
                    if (friends.contains(username)) {
                        // Makes the name bold
                        name = name.substring(0, 2) + "\u00a7l" + name.substring(2);

                        // MVP+ and MVP++ Players have extra color codes in their names which remove the bold
                        if (name.contains("+")) {
                            // Add bold color codes before the "+" and "]" characters in display names
                            int firstPlus = name.indexOf("+");
                            name = name.substring(0, firstPlus) + "\u00a7l" + name.substring(firstPlus);
                            int firstBracket = name.indexOf("]");
                            name = name.substring(0, firstBracket) + "\u00a7l" + name.substring(firstBracket);
                        }
                    }
                } else {
                    // Only start a thread to retrieve the list of usernames if one does not already exist
                    if (fetchFriendInfoThread == null) {
                        // Create and start a new thread to fetch friend info
                        fetchFriendInfoThread = new Thread(() -> {
                            friends = fetchFriendInfo();
                            fetchFriendInfoThread = null;
                        });
                        fetchFriendInfoThread.start();
                    }
                }
            }
        }

        return name;
    }

    public static boolean shouldRenderPlayerHead(NetworkPlayerInfo networkPlayerInfo) {
        return !MinecraftUtils.isHypixel() || !isSkyblockTabInformationEntry(networkPlayerInfo);
    }

    public static boolean hidePing(NetworkPlayerInfo networkPlayerInfo) {
        return MinecraftUtils.isHypixel() && ((HytilitiesConfig.hidePingInTab && !Hytilities.INSTANCE.getLobbyChecker().playerIsInLobby()) || isSkyblockTabInformationEntry(networkPlayerInfo));
    }

    private static final Pattern validMinecraftUsername = Pattern.compile("[A-Za-z0-9_]{1,16}");
    private static final Pattern skyblockTabInformationEntryGameProfileNameRegex = Pattern.compile("![A-D]-[a-v]");
    private static boolean isSkyblockTabInformationEntry(NetworkPlayerInfo networkPlayerInfo) {
        if (!HytilitiesConfig.cleanerSkyblockTabInfo) return false;
        LocrawInformation locraw = Hytilities.INSTANCE.getLocrawUtil().getLocrawInformation();
        return
            locraw != null &&
            locraw.getGameType().equals(GameType.SKYBLOCK) &&
            skyblockTabInformationEntryGameProfileNameRegex.matcher(networkPlayerInfo.getGameProfile().getName()).matches() &&
            !validMinecraftUsername.matcher(networkPlayerInfo.getDisplayName().getUnformattedText()).matches();
    }

    /**
     * List of usernames of Hypixel Friends.
     * Generated by {@link #modifyName(String)}, which runs {@link #fetchFriendInfoThread}.
     */
    private static List<String> friends = null;

    /**
     * Thread that runs {@link #fetchFriendInfo()}.
     * This thread will be created by {@link #modifyName(String)}.
     * If the thread is null, then no such process is started.
     */
    private static Thread fetchFriendInfoThread = null;

    /**
     * Fetches the friend information for the current player
     * This method is run by {@link #modifyName(String)} when it starts {@link #fetchFriendInfoThread}
     *
     * @return A list containing the friend list for a player, or null if the connection fails
     */
    private static List<String> fetchFriendInfo() {
        try {
            // Set URL to be https://api.sk1er.club/friends/(playerUUID)
            URL url = new URL("https://api.sk1er.club/friends/" + Minecraft.getMinecraft().getSession().getPlayerID());

            // Open connection with URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(true);
            connection.addRequestProperty("User-Agent", "Mozilla/4.76 (Hytilities Mod V1.0)");

            // Connection will timeout after 15 seconds
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);

            // Read output from connection
            connection.setDoOutput(true);
            InputStream is = connection.getInputStream();

            // Convert output format into a list of names
            JsonHolder holder = new JsonHolder(IOUtils.toString(is, Charset.defaultCharset()));
            List<String> friends = new ArrayList<>();
            for (String key : holder.getKeys()) {
                JsonHolder friend = holder.optJSONObject(key);
                friends.add(friend.optString("name"));
            }

            // Return the list of friends
            return friends;
        } catch (IOException e) {
            e.printStackTrace();

            // If connection fails, return null
            return null;
        }
    }
}
