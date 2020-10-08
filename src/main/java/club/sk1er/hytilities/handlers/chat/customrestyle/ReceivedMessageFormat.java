package club.sk1er.hytilities.handlers.chat.customrestyle;

import club.sk1er.hytilities.Hytilities;
import club.sk1er.hytilities.handlers.chat.modules.modifiers.CustomRestyleHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class to help create formatted messages for replacement by the user.
 *
 * @author SirNapkin1334
 * @see CustomRestyle
 * @see CustomRestyleHandler
 * @since 1.0b4
 */
public class ReceivedMessageFormat {

    /**
     * A regex used to find named capture groups in other regexes and extract their names.
     */
    private static final Pattern groupFinderPattern = Pattern.compile("\\(\\?<(\\w+)>.*?\\)");


    /**
     * The ID of the received message, for example <code>INCOMING_FRIEND_MESSAGE</code>.
     * This string may only contain capital characters and underscores.
     */
    @NotNull
    private final String id;

    /**
     * The regex used to determine whether or not this format matches a received message.
     */
    @NotNull
    private final Pattern regex;

    /**
     * The captured groups, used for the custom messages.
     */
    @NotNull
    private final String[] availableGroups;

    /**
     * Create a new {@link ReceivedMessageFormat}.
     *
     * @param id    the ID, must contain only uppercase letters and underscores
     * @param regex the regex that matches any message that this {@link ReceivedMessageFormat} represents
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public ReceivedMessageFormat(@NotNull String id, @NotNull Pattern regex) {
        if (!id.replaceAll("[^A-Z_]", "").equals(id)) {
            throw new IllegalArgumentException("Invalid ID \"" + id + "\"");
        }

        this.id = id;
        this.regex = regex;

        final Matcher matcher = groupFinderPattern.matcher(regex.toString());
        final List<String> _availableGroups = new ArrayList<>();

        while (matcher.find()) {
            final Matcher m = groupFinderPattern.matcher(matcher.group(0));
            m.matches(); // always matches if we get to this point
            _availableGroups.add(m.group(1));
        }

        availableGroups = _availableGroups.toArray(new String[0]);
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public Pattern getRegex() {
        return regex;
    }

    @NotNull
    public String[] getAvailableGroups() {
        return availableGroups;
    }


    /**
     * Collection of static methods for the deserialization of {@link ReceivedMessageFormat} objects.
     * <p>
     * We never actually write these objects to disk, we only fetch the serialized forms from <code>format.json</code>
     * so there is no need for bulk serialization functions.
     *
     * @author SirNapkin1334
     */
    public static class Serialization {

        /**
         * Bulk deserialize a {@link Map}<{@link String}, {@link String}> of ID : REGEX into a {@link Set}<{@link ReceivedMessageFormat}>.
         *
         * @param map a {@link Map}<{@link String}, {@link String}> of data in the format of ID : REGEX
         * @return a {@link Set}<{@link ReceivedMessageFormat}> containing the data converted
         */
        @NotNull
        public static Set<ReceivedMessageFormat> createBulkFromMap(@NotNull Map<String, String> map) {
            final Set<ReceivedMessageFormat> list = new HashSet<>();
            for (String string : map.keySet()) {
                list.add(new ReceivedMessageFormat(string, Pattern.compile(map.get(string))));
            }
            return list;
        }

        /**
         * Create a {@link ReceivedMessageFormat} from its ID.
         * <strong>Requires that <code>available_restyles.json</code> has already been parsed.</strong>
         *
         * @param id the {@link ReceivedMessageFormat}'s ID
         * @return a {@link ReceivedMessageFormat} created from the ID
         */
        @NotNull
        public static ReceivedMessageFormat fromId(@NotNull String id) {
            return Hytilities.INSTANCE.getHytilitiesCommand().getFormats().get(id);
        }

    }

}
