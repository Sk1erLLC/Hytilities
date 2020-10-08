package club.sk1er.hytilities.handlers.chat.modules.modifiers;

import club.sk1er.hytilities.Hytilities;
import club.sk1er.hytilities.config.HytilitiesConfig;
import club.sk1er.hytilities.handlers.chat.ChatModule;
import club.sk1er.hytilities.handlers.chat.customrestyle.CustomRestyle;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

public class CustomRestyleHandler extends ChatModule {

    private Set<CustomRestyle> replacements;

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public void onChatEvent(ClientChatReceivedEvent event) {
        for (CustomRestyle restyle : replacements) {
            restyle.replace(event);
        }
    }

    @Override
    public boolean isEnabled() {
        return HytilitiesConfig.useCustomRestyler && Hytilities.INSTANCE.isValidConfigVersion();
    }

    public void save() {
        try {
            Files.write(Paths.get("./config/hytilities_style.json"), CustomRestyle.Serialization.bulkToJsonObject(replacements).toString().getBytes());
        } catch (IOException e) {
            Hytilities.INSTANCE.getLogger().error("Failed to write style file", e);
        }
    }

    public void load() throws IOException, JsonParseException {
        replacements = CustomRestyle.Serialization.bulkFromJsonObject(
            new JsonParser().parse(
                new String(Files.readAllBytes(Paths.get("./config/hytilities_style.json")), StandardCharsets.UTF_8)
            ).getAsJsonObject());
    }

    public Set<CustomRestyle> getReplacements() {
        return replacements;
    }
}
