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
import club.sk1er.hytilities.tweaker.asm.GuiPlayerTabOverlayTransformer;
import club.sk1er.mods.core.util.MinecraftUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.objectweb.asm.tree.ClassNode;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Used in {@link GuiPlayerTabOverlayTransformer#transform(ClassNode, String)}
 */
@SuppressWarnings("unused")
public class TabChanger {
    /**
     * Returns the username of a player based on what their name displayed as in tab.
     * For example, the input "§b[MVP§c+§b] Steve §6[GUILD]" will return "Steve"
     *
     * @param displayName The name of the player as appears in tab menu
     * @return The username of the player
     */
    private static String getUsernameFromDisplayName(String displayName) {
        /*
         * The format of a username is different for different games:
         *
         * Player in lobby: §7Steve
         * Ranked player in lobby: §b[MVP§c+§b] Steve
         * Player in lobby with guild: §7Steve §6[GUILD]
         * Ranked player in lobby with guild: §b[MVP§c+§b] Steve §6[GUILD]
         * Bedwars player: §4§lR §r§4Steve
         *
         * Our strategy to determine the username is to:
         * 1. Split the username up into words separated by spaces (ex. ["§b[MVP§c+§b]", "Steve", "§6[GUILD]"])
         * 2. Determine if the first word is a rank or team indicator
         *   a. If it is, then the second word must contain the username (ex. ["§b[MVP§c+§b]", "Steve", "§6[GUILD]"] -> "Steve")
         *   b. If it isn't, then the first word must contain the username (ex. ["§7Steve", "§6[GUILD]"] -> "§7Steve")
         * 3. Strip color and formatting codes off the selected word (ex. "§7Steve" -> "Steve")
         */

        String[] words = displayName.split(" ");

        // Regex patterns to determine if first word is a rank or team indicator
        // Note: §[0-9a-f] matches any color code
        String lobbyRankRegex = "§[0-9a-f]\\[.*]";
        String bedwarsTeamRegex = "§[0-9a-f]§l.";

        // Determine which word contains the username
        String wordContainingUsername;
        if (words[0].matches(lobbyRankRegex) || words[0].matches(bedwarsTeamRegex)) {
            wordContainingUsername = words[1];
        } else {
            wordContainingUsername = words[0];
        }

        // Strip color and formatting codes from the front of the selected word
        if (wordContainingUsername.contains("§")) {
            wordContainingUsername = wordContainingUsername.substring(wordContainingUsername.lastIndexOf("§") + 2);
        }

        return wordContainingUsername;
    }

    /**
     * Adds a star to the display name of a player in Tab.
     * If the star is added before or after the name is determined by the config value of highlightFriendsInTab
     * For example, the input "§b[MVP§c+§b] Steve §6[GUILD]" will return "§9✯ §r§b[MVP§c+§b] Steve §6[GUILD]" if
     * highlightFriendsInTab is set to "Left of Name"
     *
     * @param displayName The name of the player as appears in tab menu
     * @return The displayName that was given as input but with a star added
     */
    private static String addStarToName(String displayName) {
        switch (HytilitiesConfig.highlightFriendsInTab) {
            case 1:
                return "§9✯ §r" + displayName;
            case 2:
                return displayName + "§r §9✯";
            default:
                Hytilities.INSTANCE.getLogger()
                    .warn("Method TabChanger#addStarToName called when showFriendNamesInTab was not enabled");
                return "§9✯ §r" + displayName;
        }
    }

    public static String modifyName(String name) {
        if (MinecraftUtils.isHypixel()) {
            final String originalName = name;

            if (HytilitiesConfig.hidePlayerRanksInTab && name.startsWith("[", 2)) {
                // keep the name color if player rank is removed
                // §b[MVP§c+§b] Steve
                final String color = "\u00a7" + name.charAt(1);

                // add the rank color, and trim off the player rank
                name = color + name.substring(name.indexOf("]") + 2);
            }

            if (HytilitiesConfig.hideGuildTagsInTab && name.endsWith("]")) {
                // trim off the guild tag
                // e.g. Steve §6[GUILD]
                name = name.substring(0, name.lastIndexOf("[") - 3);
            }

            if (HytilitiesConfig.highlightFriendsInTab != 0) {
                Set<String> friendList = Hytilities.INSTANCE.getFriendCache().getFriendUsernames();
                // friendList will be null if the friend list has not been cached
                if (friendList != null) {
                    String username = getUsernameFromDisplayName(originalName);
                    if (friendList.contains(username)) {
                        name = addStarToName(name);
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

    private static final Pattern validMinecraftUsername = Pattern.compile("\\w{1,16}");
    private static final Pattern skyblockTabInformationEntryGameProfileNameRegex = Pattern.compile("![A-D]-[a-v]");

    private static boolean isSkyblockTabInformationEntry(NetworkPlayerInfo networkPlayerInfo) {
        if (!HytilitiesConfig.cleanerSkyblockTabInfo) return false;
        return
            Hytilities.INSTANCE.getSkyblockChecker().isSkyblockScoreboard() &&
            skyblockTabInformationEntryGameProfileNameRegex.matcher(networkPlayerInfo.getGameProfile().getName()).matches() &&
            !validMinecraftUsername.matcher(networkPlayerInfo.getDisplayName().getUnformattedText()).matches();
    }
}
