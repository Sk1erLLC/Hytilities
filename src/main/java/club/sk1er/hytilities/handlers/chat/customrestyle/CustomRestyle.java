package club.sk1er.hytilities.handlers.chat.customrestyle;

import club.sk1er.hytilities.Hytilities;
import club.sk1er.hytilities.handlers.chat.modules.modifiers.CustomRestyleHandler;
import com.google.gson.JsonObject;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that simplifies the storage of custom chat restyles.
 *
 * @author SirNapkin1334
 * @see RecievedMessageFormat
 * @see CustomRestyleHandler
 * @since 1.0b4
 */
public class CustomRestyle {

    /**
     * Pattern for finding and getting $-insertions in Strings.
     * Currently unused, the regex is used in a {@link String#replaceAll(String, String)} call.
     */
    private static final Pattern dollarFormatExtractorPattern = Pattern.compile("(?<!\\\\)\\$\\{([A-Z_])}");


    /**
     * The {@link RecievedMessageFormat} that this {@link CustomRestyle} is assigned to.
     */
    @NotNull
    private final RecievedMessageFormat recievedMessageFormat;

    /**
     * The user-customizable String that replaces the incoming message.
     * Customization achieved by inserting <code>${&lt;name&gt;}</code> which is replaced with the captured
     * <code>&lt;name&gt;</code>.
     */
    @Nullable
    private String replacement;

    /**
     * Since {@link CustomRestyle#equals(Object)} ignores {@link CustomRestyle#replacement}, it can be useful to have a
     * {@link CustomRestyle} object with no {@link CustomRestyle#replacement} value (for example for comparisons).
     * This is really just a wrapper for {@link CustomRestyle#CustomRestyle(RecievedMessageFormat, String)} that passes
     * null to the latter.
     */
    public CustomRestyle(@NotNull RecievedMessageFormat recievedMessageFormat) {
        this(recievedMessageFormat, null);
    }

    public CustomRestyle(@NotNull RecievedMessageFormat recievedMessageFormat, @Nullable String replacement) {
        this.recievedMessageFormat = recievedMessageFormat;
        this.setReplacement(replacement);
    }

    /**
     * Takes a string, extracts the parts that will be carried over (as commanded in this object's
     * {@link RecievedMessageFormat}), and returns the user-customizable string with those parts inserted.
     *
     * @param string the string for which to extract placeholders
     * @return the user's string, with all valid placeholders filled in
     */
    @NotNull
    public String replace(@NotNull String string) {
        if (this.replacement == null) {
            return string;
        } else {
            final Matcher replacementMatcher = this.recievedMessageFormat.getRegex().matcher(string);
            if (replacementMatcher.matches()) {
                String _replacement = this.replacement;

                for (String s : this.recievedMessageFormat.getAvailableGroups()) {
                    System.out.println(s);
                    _replacement = _replacement.replaceAll("(?<!\\\\)\\$\\{" + s + "}", replacementMatcher.group(s));
                }

                return _replacement;
            } else return string;
        }
    }

    /**
     * This function makes use of references to directly update a {@link ClientChatReceivedEvent}'s message.
     *
     * @param event the {@link ClientChatReceivedEvent} that shall be updated
     */
    public void replace(@NotNull ClientChatReceivedEvent event) {
        if (this.recievedMessageFormat.getRegex().matcher(event.message.getUnformattedText()).matches()) {
            event.message = Hytilities.colorMessageWithBackslash(this.replace(event.message.getUnformattedText()));
        }
    }

    /**
     * Checks if two {@link CustomRestyle}s are equal, by comparing their {@link RecievedMessageFormat}s.
     *
     * @param o any {@link Object}
     * @return {@link boolean} determining whether or not their {@link RecievedMessageFormat}s match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return recievedMessageFormat.equals(((CustomRestyle) o).recievedMessageFormat);
    }

    @Nullable
    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(@Nullable String replacement) {
        this.replacement = replacement;
    }

    @NotNull
    public RecievedMessageFormat getRecievedMessageFormat() {
        return recievedMessageFormat;
    }


    /**
     * A set of static methods for the serialization and deserialization of {@link CustomRestyle} objects.
     * These allow converting between {@link JsonObject}s and {@link Set}<{@link CustomRestyle}> via the
     * {@link Serialization#bulkFromJsonObject(JsonObject)} and {@link Serialization#bulkToJsonObject(Set)} methods.
     *
     * @author SirNapkin1334
     */
    public static class Serialization {

        /**
         * Deserializes a {@link JsonObject} of serialized {@link CustomRestyle}s.
         * Can be used in combination with {@link Serialization#bulkToJsonObject(Set)} to save & load from JSON files.
         *
         * @param jsonObject a {@link JsonObject} of serialized {@link CustomRestyle} objects
         * @return a {@link Set}<{@link CustomRestyle}> containing all of the deserialized {@link CustomRestyle} objects
         */
        @NotNull
        public static Set<CustomRestyle> bulkFromJsonObject(@NotNull JsonObject jsonObject) {
            final Set<CustomRestyle> customRestyles = new HashSet<>();
            for (String id : jsonObject.keySet()) {
                customRestyles.add(new CustomRestyle(RecievedMessageFormat.Serialization.fromId(id), jsonObject.get(id).getAsString()));
            }
            return customRestyles;
        }

        /**
         * Serializes a {@link List}<{@link CustomRestyle}> into a large String for storage.
         * Can be used in combination with {@link Serialization#bulkFromJsonObject(JsonObject)} to
         * save & load from JSON files.
         *
         * @param customRestyles a {@link Set}<{@link CustomRestyle}> of deserialized {@link CustomRestyle} objects
         * @return a {@link JsonObject} containing all of the serialized {@link CustomRestyle} objects
         */
        @NotNull
        public static JsonObject bulkToJsonObject(@NotNull Set<CustomRestyle> customRestyles) {
            JsonObject jsonObject = new JsonObject();
            for (CustomRestyle restyle : customRestyles) {
                jsonObject.addProperty(restyle.getRecievedMessageFormat().getId(), restyle.getReplacement());
            }
            return jsonObject;
        }

    }

}
