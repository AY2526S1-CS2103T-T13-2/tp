package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.Map;

import seedu.address.logic.Messages;
import seedu.address.logic.grammars.command.Command;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Adds or updates custom key→value fields on a person.
 * Usage: {@code field <index> /<key>:<value> ...}
 */
public class FieldCommand {

    private final int oneBasedIndex;
    private final Map<String, String> pairs;

    /**
     * Creates a FieldCommand from a parsed {@link Command}.
     * @throws IllegalArgumentException if parameters/options are invalid.
     */
    public FieldCommand(Command c) {
        requireNonNull(c);
        if (!"field".equalsIgnoreCase(c.getImperative())) {
            throw new IllegalArgumentException("Wrong imperative for FieldCommand");
        }
        // Parse index (1-based)
        try {
            this.oneBasedIndex = Integer.parseInt(c.getParameter(0));
        } catch (Exception e) {
            throw new IllegalArgumentException("Missing or invalid index. Usage: field <index> /key:value ...");
        }
        // Collect /key:value options
        Map<String, String> tmp = new LinkedHashMap<>();
        for (var e : c.getAllOptions().entrySet()) {
            String k = e.getKey();
            String v = e.getValue();
            if (v != null) {
                tmp.put(k, v);
            }
        }
        if (tmp.isEmpty()) {
            throw new IllegalArgumentException(
                "Provide at least one /key:value pair. Usage: field <index> /key:value ...");
        }
        this.pairs = tmp;
    }

    /**
     * Convenience constructor used by tests.
     */
    public FieldCommand(int oneBasedIndex, Map<String, String> pairs) {
        if (oneBasedIndex <= 0 || pairs == null || pairs.isEmpty()) {
            throw new IllegalArgumentException("Index must be > 0 and at least one /key:value pair provided.");
        }
        this.oneBasedIndex = oneBasedIndex;
        this.pairs = new LinkedHashMap<>(pairs);
    }

    /**
     * Executes the command: updates the selected person's custom fields and returns a user message.
     */
    public String execute(Model model) {
        requireNonNull(model);
        var list = model.getFilteredPersonList();
        int zero = oneBasedIndex - 1;
        if (zero < 0 || zero >= list.size()) {
            throw new IllegalArgumentException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person target = list.get(zero);

        // Merge strategy: overwrite existing keys with new values, keep others.
        Map<String, String> merged = new LinkedHashMap<>(target.getCustomFields());
        for (Map.Entry<String, String> e : pairs.entrySet()) {
            String k = normalizeKey(e.getKey());
            String v = normalizeValue(e.getValue());
            validate(k, v);
            merged.put(k, v);
        }

        Person edited = target.withCustomFields(merged);
        model.setPerson(target, edited);
        // persistence handled by LogicManager or caller

        // Build feedback message
        StringBuilder sb = new StringBuilder("Added/updated field(s): ");
        boolean first = true;
        for (Map.Entry<String, String> e : pairs.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(e.getKey()).append(":").append(e.getValue());
        }
        sb.append(" for ").append(edited.getName().fullName);
        return sb.toString();
    }

    private static String normalizeKey(String key) {
        return key == null ? "" : key.trim();
    }

    private static String normalizeValue(String value) {
        return value == null ? "" : value.trim();
    }

    private static void validate(String key, String value) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be blank.");
        }
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Field value cannot be blank.");
        }
        // Optional: add length constraints if CheckStyle/enforcer requires.
    }
}

