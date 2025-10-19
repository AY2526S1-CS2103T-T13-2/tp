package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Displays the information of a person identified using it's displayed index from the address book.
 */
public class InfoViewCommand extends Command {

    public static final String COMMAND_WORD = "infoview";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Displays the information of the person identified by the index number used in the "
            + "displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_INFO_VIEW_SUCCESS = "Viewing info for Person: %1$s";

    private final Index targetIndex;

    public InfoViewCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToShow = lastShownList.get(targetIndex.getZeroBased());

        return new CommandResult(String.format(MESSAGE_INFO_VIEW_SUCCESS, Messages.format(personToShow))
                + "\n" + personToShow.getInfo().value);
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof InfoViewCommand
                && targetIndex.equals(((InfoViewCommand) other).targetIndex));
    }
}
