package cardibuddy.logic;

import static java.util.Objects.requireNonNull;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import cardibuddy.commons.core.Messages;
import cardibuddy.logic.commands.exceptions.CommandException;


public class CommandHistory {

    private static final CommandHistory commandHistory = new CommandHistory();

    private final ObservableList<String> historyList = FXCollections.observableArrayList();
    private final ObservableList<String> unmodifiableHistoryList =
            FXCollections.unmodifiableObservableList(historyList);

    private CommandHistory() {

    }

    public CommandHistory(CommandHistory commandHistory) {
        setList(commandHistory);
    }

    /**
     * Adds an {code commandString} string to the list.
     */
    public void add(String commandString) {
        requireNonNull(commandString);
        historyList.add(commandString);
    }


    /**
     * Removes the equivalent string from the list.
     * The string must exist in the list.
     */
    public void remove(String commandString) throws CommandException {
        requireNonNull(commandString);
        if (!historyList.remove(commandString)) {
            throw new CommandException(Messages.MESSAGE_INVALID_COMMAND_FORMAT);
        }
    }

    public void setList(CommandHistory replacement) {
        requireNonNull(replacement);
        historyList.setAll(replacement.historyList);
    }

    public static CommandHistory getCommandHistory() {
        return commandHistory;
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<String> getCommandHistoryList() {
        return unmodifiableHistoryList;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CommandHistory // instanceof handles nulls
                && historyList.equals(((CommandHistory) other).historyList));
    }

    @Override
    public int hashCode() {
        return historyList.hashCode();
    }

}
