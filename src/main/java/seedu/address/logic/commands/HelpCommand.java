package seedu.address.logic.commands;

import seedu.address.model.Model;

/**
 * Displays usage instructions and opens the in-app help window.
 * <p>
 * This command does not modify application state; it only returns a {@link CommandResult}
 * that signals the UI to show the help window.
 * </p>
 *
 * <pre>
 * Format: {@code help}
 * Example: {@code help}
 * </pre>
 *
 * @see CommandResult
 */
public class HelpCommand extends Command {
    /**
     * Command word used to invoke this command from the CLI.
     */
    public static final String COMMAND_WORD = "help";

    /**
     * Usage message shown when the user's input does not conform to the expected format.
     */
    public static final String MESSAGE_USAGE = "help: Shows program usage instructions.\n"
            + "Example: help";

    /**
     * Message placed in {@link CommandResult} to instruct the UI layer to open the help window.
     */
    public static final String SHOWING_HELP_MESSAGE = "Opened help window.";

    /**
    * Executes the help command.
    * @param model The model of the app (not used, but required by the API).
    * @return CommandResult with help instructions.
    */
    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(SHOWING_HELP_MESSAGE, true, false);
    }
}

