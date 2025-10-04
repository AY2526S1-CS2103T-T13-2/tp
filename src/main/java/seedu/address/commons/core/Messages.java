package seedu.address.commons.core;

import seedu.address.model.person.Person;

/**
 * Contains utility messages and helpers for the UI and commands.
 */
public final class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d persons listed!";

    private Messages() {}

    /** Returns a one-liner describing the person for success messages. */
    public static String format(Person person) {
        return String.format(
                "%s Phone: %s Email: %s Address: %s",
                person.getName(), person.getPhone(), person.getEmail(), person.getAddress());
    }
}

