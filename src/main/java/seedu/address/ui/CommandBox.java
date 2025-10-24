package seedu.address.ui;

import java.util.Objects;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.history.CommandHistory;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;
    private final HistorySupplier historySupplier;
    private boolean navigatingHistory;

    private final HistoryNavigator historyNavigator;

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor, HistorySupplier historySupplier) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.historySupplier = Objects.requireNonNull(historySupplier);
        this.historyNavigator = new HistoryNavigator();
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> {
            setStyleToDefault();
            if (!navigatingHistory) {
                historyNavigator.reset(historySupplier.getHistory().getEntries());
            }
        });

        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::handleHistoryNavigation);
        historyNavigator.reset(historySupplier.getHistory().getEntries());
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }

        try {
            commandExecutor.execute(commandText);
            setCommandText("");
            historyNavigator.reset(historySupplier.getHistory().getEntries());
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
            historyNavigator.reset(historySupplier.getHistory().getEntries());
        }
    }

    private void handleHistoryNavigation(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            java.util.Optional<String> prev = historyNavigator.previous();
            if (prev.isPresent()) {
                setCommandText(prev.get());
            }
            event.consume();
        } else if (event.getCode() == KeyCode.DOWN) {
            java.util.Optional<String> next = historyNavigator.next();
            if (next.isPresent()) {
                setCommandText(next.get());
            } else {
                setCommandText("");
            }
            event.consume();
        }
    }



    private void setCommandText(String text) {
        navigatingHistory = true;
        commandTextField.setText(text);
        commandTextField.positionCaret(commandTextField.getText().length());
        navigatingHistory = false;
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

    /** Supplies snapshots of the command history for navigation. */
    @FunctionalInterface
    public interface HistorySupplier {
        CommandHistory getHistory();
    }

}
