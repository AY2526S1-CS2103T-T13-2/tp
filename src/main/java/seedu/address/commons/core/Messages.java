package seedu.address.commons.core;

import static java.util.Objects.requireNonNull;

import seedu.address.model.person.Person;

public class Messages {
    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    // NOTE: there is one space before the newline
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d persons listed!";

    /**
     * Formats a {@link Person} into the short summary used in success messages.
     * Example output: "Alex Yeoh Phone: 87438807 Email: alexyeoh@example.com Address: Blk 30 Geylang ...".
     * (No "Person: " prefix here — the caller's message template already includes that.)
     */
    public static String format(Person person) {
        requireNonNull(person);
        return new StringBuilder()
                .append(person.getName())
                .append(" Phone: ").append(person.getPhone())
                .append(" Email: ").append(person.getEmail())
                .append(" Address: ").append(person.getAddress())
                .toString();
    }
}

