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

package club.sk1er.hytilities;

import club.sk1er.hytilities.command.HytilitiesCommand;
import club.sk1er.hytilities.command.SilentRemoveCommand;
import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatHandler;
import club.sk1er.hytilities.handlers.chat.modules.triggers.AutoQueue;
import club.sk1er.hytilities.handlers.chat.modules.events.AchievementEvent;
import club.sk1er.hytilities.handlers.chat.modules.events.LevelupEvent;
import club.sk1er.hytilities.handlers.game.hardcore.HardcoreStatus;
import club.sk1er.hytilities.handlers.general.AutoStart;
import club.sk1er.hytilities.handlers.general.CommandQueue;
import club.sk1er.hytilities.handlers.language.LanguageHandler;
import club.sk1er.hytilities.handlers.lobby.LobbyChecker;
import club.sk1er.hytilities.handlers.lobby.bossbar.LobbyBossbar;
import club.sk1er.hytilities.handlers.lobby.limbo.LimboLimiter;
import club.sk1er.hytilities.handlers.lobby.npc.NPCHider;
import club.sk1er.hytilities.handlers.silent.SilentRemoval;
import club.sk1er.hytilities.tweaker.asm.GuiIngameForgeTransformer;
import club.sk1er.hytilities.util.locraw.LocrawUtil;
import club.sk1er.modcore.ModCoreInstaller;
import club.sk1er.mods.core.gui.notification.Notifications;
import club.sk1er.mods.core.universal.ChatColor;
import club.sk1er.mods.core.util.MinecraftUtils;
import club.sk1er.mods.core.util.Multithreading;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.objectweb.asm.tree.ClassNode;

@Mod(
    modid = Hytilities.MOD_ID,
    name = Hytilities.MOD_NAME,
    version = Hytilities.VERSION
)
public class Hytilities {

    public static final String MOD_ID = "hytilities";
    public static final String MOD_NAME = "Hytilities";
    public static final String VERSION = "0.1";

    private Map<String, String> restyleMeta = new HashMap<>();
    private boolean validConfigVersion;

    private static final String[] ACCEPTED_CONFIG_VERSIONS = {"1"};

    @Mod.Instance(MOD_ID)
    public static Hytilities INSTANCE;

    private static final Logger logger = LogManager.getLogger("Hytilities");

    private final HytilitiesConfig config = new HytilitiesConfig();

    private HytilitiesCommand hytilitiesCommand;

    private LanguageHandler languageHandler;
    private HardcoreStatus hardcoreStatus;
    private SilentRemoval silentRemoval;
    private CommandQueue commandQueue;
    private LobbyChecker lobbyChecker;
    private ChatHandler chatHandler;
    private LocrawUtil locrawUtil;
    private AutoQueue autoQueue;

    private boolean loadedCall;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModCoreInstaller.initializeModCore(Minecraft.getMinecraft().mcDataDir);
        config.preload();

        ClientCommandHandler.instance.registerCommand(hytilitiesCommand = new HytilitiesCommand());
        ClientCommandHandler.instance.registerCommand(new SilentRemoveCommand());

        registerHandlers();

        getAvailableRestyles(false, true);
    }

    @Mod.EventHandler
    public void finishedStarting(FMLLoadCompleteEvent event) {
        this.loadedCall = true;
        if (!validConfigVersion) {
            Notifications.INSTANCE.pushNotification("Outdated Version!",
                "Your Hytilities version is outdated. Please update it in order to use custom restyling. " +
                    "Check your log for more details.");
        }
    }


    private void registerHandlers() {
        // general stuff
        MinecraftForge.EVENT_BUS.register(locrawUtil = new LocrawUtil());
        MinecraftForge.EVENT_BUS.register(autoQueue = new AutoQueue());
        MinecraftForge.EVENT_BUS.register(commandQueue = new CommandQueue());
        MinecraftForge.EVENT_BUS.register(new AutoStart());

        // chat
        MinecraftForge.EVENT_BUS.register(silentRemoval = new SilentRemoval());
        MinecraftForge.EVENT_BUS.register(hardcoreStatus = new HardcoreStatus());
        MinecraftForge.EVENT_BUS.register(chatHandler = new ChatHandler());
        MinecraftForge.EVENT_BUS.register(new AchievementEvent());
        MinecraftForge.EVENT_BUS.register(new LevelupEvent());

        // lobby
        MinecraftForge.EVENT_BUS.register(lobbyChecker = new LobbyChecker());
        MinecraftForge.EVENT_BUS.register(new NPCHider());
        MinecraftForge.EVENT_BUS.register(new LobbyBossbar());
        MinecraftForge.EVENT_BUS.register(new LimboLimiter());

        // language
        MinecraftForge.EVENT_BUS.register(languageHandler = new LanguageHandler());
    }

    // Adapted from AutoGG
    // https://github.com/Sk1erLLC/AutoGG/blob/master/src/main/java/club/sk1er/autogg/AutoGG.java#L84
    public void getAvailableRestyles(boolean sendChatMsg, boolean load) {
        Multithreading.runAsync(() -> {
            try {
                validConfigVersion = true;

                restyleMeta.put("type", "REMOTE");

                evaluateJson(new JsonParser().parse(
                    fetchString("https://static.sk1er.club/hytilities/available_restyles.json")
                ).getAsJsonObject(), load);

            } catch (IOException e) {
                if (sendChatMsg) {
                    sendMessage(ChatColor.RED + "Unable to fetch remote restyle list! Do you have an internet connection?");
                    sendMessage(ChatColor.YELLOW + "Using fallback local restyle list.");
                }

                if (e instanceof FileNotFoundException) {
                    logger.warn("No internet connection - using fallback local restyle list.");
                } else {
                    logger.error("Failed to fetch remote restyle list.", e);
                }

                try {
                    restyleMeta.put("type", "LOCAL");
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/available_restyles.json")))) {
                        evaluateJson(new JsonParser().parse(reader.lines().collect(Collectors.joining("\n"))).getAsJsonObject(), load);
                    }
                } catch (IOException ee) {
                    if (sendChatMsg) {
                        sendMessage(ChatColor.RED + ChatColor.BOLD.toString() +
                            "FAILED LOADING RESTYLES FROM INSIDE JAR! SOMETHING HAS GONE TERRIBLY, TERRIBLY WRONG!");
                    }
                    logger.error(
                        "FAILED LOADING RESTYLES FROM INSIDE JAR! SOMETHING HAS GONE TERRIBLY, TERRIBLY WRONG!",
                        ee);
                    validConfigVersion = false;
                } catch (JsonSyntaxException ee) {
                    if (sendChatMsg) {
                        sendMessage(ChatColor.RED + ChatColor.BOLD.toString() +
                            "Local JSON Syntax Error! Contact the mod authors if you see this message! https://sk1er.club/support-discord");
                    }
                    logger.error("Local JSON Syntax Error! Contact us in the support channel at https://sk1er.club/support-discord.", e);
                    validConfigVersion = false;
                } catch (NullPointerException ee) {
                    if (sendChatMsg) {
                        sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Unsupported local restyle list version! A developer screwed up!");
                    }

                    logger.error("Unsupported local restyle list version! A developer screwed up!");
                    validConfigVersion = false;
                }


            } catch (JsonSyntaxException e) {
                if (sendChatMsg) {
                    sendMessage(ChatColor.RED + ChatColor.BOLD.toString() +
                        "JSON Syntax Error! Contact the mod authors if you see this message! https://sk1er.club/support-discord");
                }
                logger.error("JSON Syntax Error! Contact us in the support channel at https://sk1er.club/support-discord.", e);
                validConfigVersion = false;
            } catch (NullPointerException e) { // catch NPE because it can happen in multiple locations
                if (sendChatMsg) {
                    sendMessage(ChatColor.RED + "Unsupported remote restyle list version! Please update Hytilities!");
                }

                logger.error("Unsupported remote restyle list version! Please update Hytilities!");
                validConfigVersion = false;
            }
        });
    }

    private void evaluateJson(JsonObject json, boolean load) {
        if (!Arrays.asList(ACCEPTED_CONFIG_VERSIONS).contains(json.get("config_version").getAsString())) {
            throw new NullPointerException("This will be handled internally.");
        }

        restyleMeta = jsonToMap(json.get("meta"));

        hytilitiesCommand.updateFormats(jsonToMap(json.get("restyles")));

        if (load) {
            try {
                chatHandler.getCustomChatFormat().load();
            } catch (IOException e) {
                if (!(e instanceof FileNotFoundException)) {
                    Hytilities.INSTANCE.getLogger().error("Failed loading style file.", e);
                }
            } catch (JsonParseException e) {
                Hytilities.INSTANCE.getLogger().error("Invalid JSON in style file. Did you modify it?", e);
            }
        }

    }

    public static IChatComponent colorMessage(String message) {
        return new ChatComponentText(ChatColor.translateAlternateColorCodes('&', message));
    }

    /** {@link Hytilities#colorMessage(String)} is faster, but this is better for user input. */
    public static IChatComponent colorMessageWithBackslash(String message) {
        return new ChatComponentText(message.replaceAll("(?i)(?<!\\\\)&(\\da-fk-or)", "\u00a7$1"));
    }

    public static void sendMessage(String message) {
        MinecraftUtils.sendMessage(ChatColor.GOLD + "[Hytilities] ", ChatColor.translateAlternateColorCodes('&', message));
    }

    // https://stackoverflow.com/a/21720953
    @NotNull
    public static <M extends Map<?, ?>> M jsonToMap(@NotNull JsonElement json) {
        return new Gson().fromJson(json, new TypeToken<M>() {}.getType());
    }

    public HytilitiesConfig getConfig() {
        return config;
    }

    public LocrawUtil getLocrawUtil() {
        return locrawUtil;
    }

    public SilentRemoval getSilentRemoval() {
        return silentRemoval;
    }

    public LobbyChecker getLobbyChecker() {
        return lobbyChecker;
    }

    /**
     * Used in {@link GuiIngameForgeTransformer#transform(ClassNode, String)}
     */
    @SuppressWarnings({"unused", "RedundantSuppression"})
    public HardcoreStatus getHardcoreStatus() {
        return hardcoreStatus;
    }

    public AutoQueue getAutoQueue() {
        return autoQueue;
    }

    public boolean isLoadedCall() {
        return loadedCall;
    }

    public void setLoadedCall(boolean loadedCall) {
        this.loadedCall = loadedCall;
    }

    public boolean isValidConfigVersion() {
        return validConfigVersion;
    }

    public Map<String, String> getRestyleMeta() {
        return restyleMeta;
    }

    @NotNull
    public static String fetchString(String url) throws IOException {
        String content;

        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.addRequestProperty("User-Agent", "Mozilla/4.76 (Sk1er Hytilities)");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);

            try (InputStream setup = connection.getInputStream()) {
                content = IOUtils.toString(setup, Charset.defaultCharset());
            }
        } catch (Exception e) {
            logger.error("Failed to fetch string.", e);
            throw new IOException("Failed to fetch string.", e);
        }

        return content;
    }

    public Logger getLogger() {
        return logger;
    }

    public HytilitiesCommand getHytilitiesCommand() {
        return hytilitiesCommand;
    }

    public CommandQueue getCommandQueue() {
        return commandQueue;
    }

    public LanguageHandler getLanguageHandler() {
        return languageHandler;
    }

    public ChatHandler getChatHandler() {
        return chatHandler;
    }
}
