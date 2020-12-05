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
import club.sk1er.mods.core.util.Multithreading;
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

                    /*
                     * Uses regex on the first word in the display name to determine which part of the name is the username
                     * Regex will match the username if:
                     * 1. The name contains [ and ] (with any number of characters separating them)
                     *    If this is true, that means that the first word in the name is probably a prefix
                     *    such as §a[VIP] or §b[MVP§c+§b]
                     * 2. The name is a team identifier in the format §(hex character)§l(any character)
                     *    This form looks like a colored and bold single character
                     *    In games like Bedwars, players will have this to represent their team
                     *    For example, a player on the red team might show as "§4§lR §r§4Steve" which looks like "R Steve"
                     * If the string matches either of the cases, then we should use the second word instead
                     * Examples:
                     * "§7Steve §6[GUILD]" -> First term is c§7Steve - Does not match regex
                     * "§b[MVP§c+§b] Steve" -> First term is §b[MVP§c+§b] - Matches regex because it contains [ and ]
                     * "§4§lR §r§4Steve" -> First term is §4§lR - Matches regex because it contains the team identifier
                     */
                    String username;
                    if (segments[0].matches("\\[.*]|§[0-9a-f]§l.")) {
                        // If the first term is a prefix they should have more than one part to their username
                        assert segments.length > 1;
                        // Define their username to be the second term (i.e. the term after the prefix)
                        username = segments[1];
                    } else {
                        // First term is not a prefix, that term is probably the prefix
                        username = segments[0];
                    }

                    /*
                     * The username may still contain color codes
                     * This will eliminate all the color codes by cutting off all characters belonging to color codes
                     */
                    if (username.contains("§")) {
                        username = username.substring(username.lastIndexOf("§") + 2);
                    }

                    // If user is a friend
                    if (friends.contains(username)) {
                        // For every word of the username, insert §l (the bold color code) after the last color code
                        StringBuilder sb = new StringBuilder(segments.length * 4);
                        for (String s : segments) {
                            int position = s.contains("§") ? s.lastIndexOf("§") + 2 : 0;
                            sb.append(s.substring(0, position))
                                .append("§l")
                                .append(s.substring(position))
                                .append(" ");
                        }
                        name = sb.toString();

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
                    if (!fetchingFriendInfo) {
                        fetchingFriendInfo = true;
                        // Create and start a new thread to fetch friend info
                        Multithreading.runAsync(new Thread(() -> {
                            friends = fetchFriendInfo();
                            fetchingFriendInfo = false;
                        }));
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
     * Generated by {@link #modifyName(String)}, which runs a thread
     */
    private static List<String> friends = null;

    /**
     * Thread that runs {@link #fetchFriendInfo()}.
     * This thread will be created by {@link #modifyName(String)}.
     * If the thread is null, then no such process is started.
     */
    private static boolean fetchingFriendInfo = false;

    /**
     * Fetches the friend information for the current player.
     * This method is run by {@link #modifyName(String)} when it starts a thread
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
            try (InputStream is = connection.getInputStream()) {
                // Convert output format into a list of names
                JsonHolder holder = new JsonHolder(IOUtils.toString(is, Charset.defaultCharset()));
                List<String> friends = new ArrayList<>();
                for (String key : holder.getKeys()) {
                    JsonHolder friend = holder.optJSONObject(key);
                    friends.add(friend.optString("name"));
                }

                // Return the list of friends
                return friends;
            }
        } catch (IOException e) {
            e.printStackTrace();

            // If connection fails, return null
            return null;
        }
    }
}
