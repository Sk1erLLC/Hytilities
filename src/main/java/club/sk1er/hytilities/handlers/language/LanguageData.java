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

package club.sk1er.hytilities.handlers.language;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Data class for storing the Regex's for each Hypixel language.
 *
 * @author Koding
 */
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "DuplicatedCode"})
public class LanguageData {

    /**
     * GSON deserialization fields which are loaded in when the file is parsed.
     */
    public String autoQueuePrefix = "You died! Want to play again?";
    public String autoQueueClick = "Click here!";

    private String chatCleanerJoinNormal = "joined the lobby";
    private String chatCleanerJoinHalloween = "spooked into the lobby";
    private String chatCleanerJoinChristmas = "sled into the lobby";
    private String chatCleanerMysteryBoxFind = "^(?<player>\\w{1,16}) found a \u2730{5} Mystery Box!$";
    private String chatCleanerSoulWellFind = "^.+ has found .+ in the Soul Well!$";
    private String chatCleanerGameAnnouncement = "^\u27A4 A .+ game is (?:available to join|starting in .+ seconds)! CLICK HERE to join!$";
    private String chatCleanerBedwarsPartyAdvertisement = "(?<number>[1-3]/[2-4])";
    private String chatCleanerConnectionStatus = "^(?:Friend|Guild) > (?<player>\\w{1,16}) (?:joined|left)\\.$";
    private String chatCleanerMvpEmotes = "\u00A7r\u00A7(?:c\u2764|6\u272E|a\u2714|c\u2716|b\u2615|e\u279C|e\u00AF\\\\_\\(\u30C4\\)_/\u00AF|c\\(\u256F\u00B0\u25A1\u00B0\uFF09\u256F\u00A7r\u00A7f\uFE35\u00A7r\u00A77 \u253B\u2501\u253B|d\\( \uFF9F\u25E1\uFF9F\\)/|a1\u00A7r\u00A7e2\u00A7r\u00A7c3|b\u2609\u00A7r\u00A7e_\u00A7r\u00A7b\u2609|e\u270E\u00A7r\u00A76\\.\\.\\.|a\u221A\u00A7r\u00A7e\u00A7l\\(\u00A7r\u00A7a\u03C0\u00A7r\u00A7a\u00A7l\\+x\u00A7r\u00A7e\u00A7l\\)\u00A7r\u00A7a\u00A7l=\u00A7r\u00A7c\u00A7lL|e@\u00A7r\u00A7a'\u00A7r\u00A7e-\u00A7r\u00A7a'|6\\(\u00A7r\u00A7a0\u00A7r\u00A76\\.\u00A7r\u00A7ao\u00A7r\u00A7c\\?\u00A7r\u00A76\\)|b\u0F3C\u3064\u25D5_\u25D5\u0F3D\u3064|e\\(\u00A7r\u00A7b'\u00A7r\u00A7e-\u00A7r\u00A7b'\u00A7r\u00A7e\\)\u2283\u00A7r\u00A7c\u2501\u00A7r\u00A7d\u2606\uFF9F\\.\\*\uFF65\uFF61\uFF9F|e\u2694|a\u270C|c\u00A7lOOF|e\u00A7l<\\('O'\\)>|a\\^-\\^|a\\^_\\^|b\u2603|9\u30FD\u00A7r\u00A75\\(\u00A7r\u00A7d\u2310\u00A7r\u00A7c\u25A0\u00A7r\u00A76_\u00A7r\u00A7e\u25A0\u00A7r\u00A7b\\)\u00A7r\u00A73\u30CE\u00A7r\u00A79\u266C|d<\u00A7r\u00A7eo\u00A7r\u00A7d/|e= \u00A7r\u00A7b\uFF3E\u25CF \u22CF \u25CF\uFF3E\u00A7r\u00A7e =|a\u30FD \\(\u25D5\u25E1\u25D5\\) \uFF89|e\\(\u00A7r\u00A7a\u273F\u00A7r\u00A7e\u25E0\u203F\u25E0\\)|6\\(\u00A7r\u00A78\u30FB\u00A7r\u00A76\u229D\u00A7r\u00A78\u30FB\u00A7r\u00A76\\)|e\u30FD\\(\\^\u25C7\\^\\*\\)/|6\\(\u1D54\u1D25\u1D54\\))\u00A7r";
    public String chatCleanerHypeLimit = "  \u27A4 You have reached your Hype limit!";
    private String chatGiftBlocker = "They have gifted \\d+ ranks so far!";

    private String connectedServerConnectMessage = "^(You are currently connected to server \\S+)|(Sending you to \\S+.{3}!)$";

    private String achievementPattern = "a>> {3}Achievement Unlocked: (?<achievement>.+) {3}<<a";
    private String levelUpPattern = "You are now Hypixel Level (?<level>\\d+)!";
    private String guildPlayerJoinPattern = "^(?:\\[.*] )?(?<player>\\S{1,16}) joined the guild!$";

    private String chatRestylerGameJoinStyle = "^§r§(?<color>[\\da-f])(?:§k)?(?<player>\\w{1,16})§r§e has joined (?<amount>.+)!§r$";
    private String chatRestylerGameLeaveStyle = "^§r§(?<color>[\\da-f])(?:§k)?(?<player>\\w{1,16})§r§e has quit!§r$";
    private String chatRestylerGameStartCounterStyle = "^(?<title>(The game starts in|Cages open in:|You will respawn in|The Murderer gets their sword in|You get your sword in)) (?<time>\\d{1,3}) (?<unit>(seconds?!))$"; // todo please translate "Cages open in:" to french (also translate to chatRestylerGameStartCounterOutputStyle)
    private String chatRestylerGameStartCounterOutputStyle = "^\u00a7e\u00a7l\\* \u00a7a(The game starts in|Cages open in:) \u00a7b\u00a7l\\d{1,3} \u00a7aseconds?!\u00a7r$";
    private String chatRestylerFormattedPaddingPattern = "\\(§r§b(\\d{1,2})§r§e/§r§b(\\d{1,3})§r§e\\)";
    private String chatRestylerPartyPattern = "^((?:\\u00a7r)?\\u00a7\\w)(Party )(\\u00a7\\w>)";
    private String chatRestylerGuildPattern = "^((?:\\u00a7r)?\\u00a7\\w)(Guild >)";
    private String chatRestylerFriendPattern = "^((?:\\u00a7r)?\\u00a7\\w)(Friend >)";
    private String chatRestylerOfficerPattern = "^((?:\\u00a7r)?\\u00a7\\w)(Officer >)";


    private String autoChatSwapperPartyStatus = "^(?:You have been kicked from the party by (?:\\[.+] )?\\w{1,16}|(?:\\[.+] )?\\w{1,16} has disbanded the party!|You left the party.)$";
    private String autoChatSwapperChannelSwap = "^You are now in the (?<channel>ALL|GUILD|OFFICER) channel$";
    public String autoChatSwapperAlreadyInChannel = "You're already in this channel!";

    private String whiteChatNonMessage = "(?<prefix>.+)§7: (?<message>.*)";
    private String privateMessageWhiteChat = "^(?<type>§dTo|§dFrom) (?<prefix>.+): §r(?<message>§7.*)(?:§r)?$";

    public String limboLimiterSpawned = "You were spawned in Limbo.";
    public String limboLimiterAfk = "You are AFK. Move around to return from AFK.";

    private String silentRemovalLeaveMessage = "(?:Friend|Guild) > (?<player>\\w{1,16}) left\\.";

    public String noSpectatorCommands = "You are not allowed to use commands as a spectator!";

    public String cannotShoutBeforeSkywars = "You can't shout until the game has started!";
    public String cannotShoutBeforeGame = "You can't use /shout before the game has started.";
    public String cannotShoutAfterGame = "You can't use /shout after the game has finished.";

    private String hypixelLevelUp = "You are now Hypixel Level (?<level>\\d+)!";

    /**
     * Cached values which use the messages read from the config file.
     * Particularly Regexes.
     */
    public Set<String> chatCleanerJoinMessageTypes;

    public Pattern chatCleanerMysteryBoxFindRegex;
    public Pattern chatCleanerSoulWellFindRegex;
    public Pattern chatCleanerGameAnnouncementRegex;
    public Pattern chatCleanerBedwarsPartyAdvertisementRegex;
    public Pattern chatCleanerConnectionStatusRegex;
    public Pattern chatCleanerMvpEmotesRegex;
    public Pattern chatGiftBlockerRegex;

    public Pattern connectedServerConnectMessageRegex;

    public Pattern achievementRegex;
    public Pattern levelUpRegex;
    public Pattern guildPlayerJoinRegex;

    public Pattern chatRestylerGameJoinStyleRegex;
    public Pattern chatRestylerGameLeaveStyleRegex;
    public Pattern chatRestylerGameStartCounterStyleRegex;
    public Pattern chatRestylerGameStartCounterOutputStyleRegex;
    public Pattern chatRestylerFormattedPaddingPatternRegex;
    public Pattern chatRestylerPartyPatternRegex;
    public Pattern chatRestylerGuildPatternRegex;
    public Pattern chatRestylerFriendPatternRegex;
    public Pattern chatRestylerOfficerPatternRegex;

    public Pattern autoChatSwapperPartyStatusRegex;
    public Pattern autoChatSwapperChannelSwapRegex;

    public Pattern whiteChatNonMessageRegex;
    public Pattern privateMessageWhiteChatRegex;
    public Pattern silentRemovalLeaveMessageRegex;

    public Pattern hypixelLevelUpRegex;

    /**
     * Compiles all the required patterns and caches them for later use.
     */
    public void initialize() {
        chatCleanerJoinMessageTypes = Sets.newHashSet(chatCleanerJoinNormal, chatCleanerJoinHalloween, chatCleanerJoinChristmas);

        chatCleanerMysteryBoxFindRegex = Pattern.compile(chatCleanerMysteryBoxFind);
        chatCleanerSoulWellFindRegex = Pattern.compile(chatCleanerSoulWellFind);
        chatCleanerGameAnnouncementRegex = Pattern.compile(chatCleanerGameAnnouncement);
        chatCleanerBedwarsPartyAdvertisementRegex = Pattern.compile(chatCleanerBedwarsPartyAdvertisement);
        chatCleanerConnectionStatusRegex = Pattern.compile(chatCleanerConnectionStatus);
        chatCleanerMvpEmotesRegex = Pattern.compile(chatCleanerMvpEmotes);
        chatGiftBlockerRegex = Pattern.compile(chatGiftBlocker);

        connectedServerConnectMessageRegex = Pattern.compile(connectedServerConnectMessage);

        achievementRegex = Pattern.compile(achievementPattern);
        levelUpRegex = Pattern.compile(levelUpPattern);
        guildPlayerJoinRegex = Pattern.compile(guildPlayerJoinPattern);

        chatRestylerGameJoinStyleRegex = Pattern.compile(chatRestylerGameJoinStyle);
        chatRestylerGameLeaveStyleRegex = Pattern.compile(chatRestylerGameLeaveStyle);
        chatRestylerGameStartCounterStyleRegex = Pattern.compile(chatRestylerGameStartCounterStyle);
        chatRestylerGameStartCounterOutputStyleRegex = Pattern.compile(chatRestylerGameStartCounterOutputStyle);
        chatRestylerFormattedPaddingPatternRegex = Pattern.compile(chatRestylerFormattedPaddingPattern);
        chatRestylerPartyPatternRegex = Pattern.compile(chatRestylerPartyPattern);
        chatRestylerGuildPatternRegex = Pattern.compile(chatRestylerGuildPattern);
        chatRestylerFriendPatternRegex = Pattern.compile(chatRestylerFriendPattern);
        chatRestylerOfficerPatternRegex = Pattern.compile(chatRestylerOfficerPattern);

        autoChatSwapperPartyStatusRegex = Pattern.compile(autoChatSwapperPartyStatus);
        autoChatSwapperChannelSwapRegex = Pattern.compile(autoChatSwapperChannelSwap);

        whiteChatNonMessageRegex = Pattern.compile(whiteChatNonMessage);
        privateMessageWhiteChatRegex = Pattern.compile(privateMessageWhiteChat);
        silentRemovalLeaveMessageRegex = Pattern.compile(silentRemovalLeaveMessage);

        hypixelLevelUpRegex = Pattern.compile(hypixelLevelUp);
    }
}
