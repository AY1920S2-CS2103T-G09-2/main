package cardibuddy.logic.parser;

import static cardibuddy.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static cardibuddy.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cardibuddy.logic.LogicToUiManager;
import cardibuddy.logic.commands.AddCommand;
import cardibuddy.logic.commands.AnswerCommand;
import cardibuddy.logic.commands.ClearCommand;
import cardibuddy.logic.commands.Command;
import cardibuddy.logic.commands.DeleteCommand;
import cardibuddy.logic.commands.EditCommand;
import cardibuddy.logic.commands.ExitCommand;
import cardibuddy.logic.commands.FilterCommand;
import cardibuddy.logic.commands.ForceCommand;
import cardibuddy.logic.commands.HelpCommand;
import cardibuddy.logic.commands.ListCommand;
import cardibuddy.logic.commands.NextCommand;
import cardibuddy.logic.commands.OpenCommand;
import cardibuddy.logic.commands.QuitCommand;
import cardibuddy.logic.commands.SearchCardCommand;
import cardibuddy.logic.commands.SearchCommand;
import cardibuddy.logic.commands.SearchDeckCommand;
import cardibuddy.logic.commands.StatisticsCommand;
import cardibuddy.logic.commands.TestCommand;
import cardibuddy.logic.parser.exceptions.ParseException;
import cardibuddy.model.ReadOnlyCardiBuddy;

/**
 * Parses user input.
 */
public class CardiBuddyParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private ReadOnlyCardiBuddy cardiBuddy;
    private LogicToUiManager logicToUiManager;

    public CardiBuddyParser(ReadOnlyCardiBuddy cardiBuddy) {
        this.cardiBuddy = cardiBuddy;
    }

    public void setLogicToUiManager(LogicToUiManager logicToUiManager) {
        this.logicToUiManager = logicToUiManager;
    }

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        switch (commandWord) {

        case OpenCommand.COMMAND_WORD:
            return new OpenCommandParser(logicToUiManager).parse(arguments);

        case AddCommand.COMMAND_WORD:
            return new AddCommandParser(cardiBuddy, logicToUiManager).parse(arguments);

        case EditCommand.COMMAND_WORD:
            return new EditCommandParser().parse(arguments);

        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommandParser(logicToUiManager).parse(arguments);

        case TestCommand.COMMAND_WORD: // test session command
            return new TestCommandParser(logicToUiManager).parse(arguments);

        case AnswerCommand.COMMAND_WORD: // test session command
            return new AnswerCommand(logicToUiManager, arguments.trim());

        case NextCommand.COMMAND_WORD: // test session command
            return new NextCommand(logicToUiManager);

        case QuitCommand.COMMAND_WORD: // test session command
            return new QuitCommand(logicToUiManager);

        case ForceCommand.COMMAND_WORD: // test session command
            return new ForceCommand();

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case FilterCommand.COMMAND_WORD:
            return new FilterCommandParser().parse(arguments);

        case SearchCommand.COMMAND_WORD:
            switch (arguments.substring(1, 5)) {

            case SearchDeckCommand.COMMAND_WORD:
                return new SearchDeckCommandParser().parse(arguments.substring(5));

            case SearchCardCommand.COMMAND_WORD:
                return new SearchCardCommandParser(logicToUiManager).parse(arguments.substring(5));

            default:
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);

            }

        case StatisticsCommand.COMMAND_WORD:
            return new StatisticsCommandParser(logicToUiManager).parse(arguments);

        case ListCommand.COMMAND_WORD:
            return new ListCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }
}