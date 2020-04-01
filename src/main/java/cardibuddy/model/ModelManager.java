package cardibuddy.model;

import static cardibuddy.commons.util.CollectionUtil.requireAllNonNull;
import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.logging.Logger;

import cardibuddy.commons.core.GuiSettings;
import cardibuddy.commons.core.LogsCenter;
import cardibuddy.model.deck.Deck;
import cardibuddy.model.flashcard.Flashcard;
import cardibuddy.model.flashcard.Question;
import cardibuddy.model.testsession.TestResult;
import cardibuddy.model.testsession.TestSession;
import cardibuddy.model.testsession.exceptions.AlreadyCorrectException;
import cardibuddy.model.testsession.exceptions.EmptyDeckException;
import cardibuddy.model.testsession.exceptions.NoOngoingTestException;
import cardibuddy.model.testsession.exceptions.UnansweredQuestionException;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;


/**
 * Represents the in-memory model of the cardibuddy data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(cardibuddy.model.ModelManager.class);

    private final CardiBuddy cardiBuddy;
    private final UserPrefs userPrefs;
    private final FilteredList<Flashcard> filteredFlashcards;
    private final FilteredList<Deck> filteredDecks;
    private TestSession testSession;

    /**
     * Initializes a ModelManager with the given cardiBuddy and userPrefs.
     */
    public ModelManager(ReadOnlyCardiBuddy cardiBuddy, ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(cardiBuddy, userPrefs);

        logger.fine("Initializing with CardiBuddy: " + cardiBuddy + " and user prefs " + userPrefs);

        this.cardiBuddy = new CardiBuddy(cardiBuddy);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredFlashcards = new FilteredList<>(this.cardiBuddy.getFlashcardList());
        filteredDecks = new FilteredList<>(this.cardiBuddy.getDeckList());
    }

    public ModelManager() {
        this(new CardiBuddy(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getCardiBuddyFilePath() {
        return userPrefs.getCardiBuddyFilePath();
    }

    @Override
    public void setCardiBuddyFilePath(Path cardiBuddyFilePath) {
        requireNonNull(cardiBuddyFilePath);
        userPrefs.setCardiBuddyFilePath(cardiBuddyFilePath);
    }

    //=========== CardiBuddy ================================================================================

    @Override
    public void setCardiBuddy(ReadOnlyCardiBuddy cardiBuddy) {
        this.cardiBuddy.resetData(cardiBuddy);
    }

    @Override
    public ReadOnlyCardiBuddy getCardiBuddy() {
        return cardiBuddy;
    }

    @Override
    public boolean hasDeck(Deck deck) {
        requireNonNull(deck);
        return cardiBuddy.hasDeck(deck);
    }

    @Override
    public void deleteDeck(Deck target) {
        cardiBuddy.removeDeck(target);
    }

    @Override
    public void addDeck(Deck deck) {
        cardiBuddy.addDeck(deck);
        updateFilteredDeckList(PREDICATE_SHOW_ALL_DECKS);
    }

    @Override
    public void setDeck(Deck target, Deck editedDeck) {
        cardiBuddy.setDeck(target, editedDeck);
    }

    @Override
    public boolean hasFlashcard(Flashcard flashcard) {
        requireNonNull(flashcard);
        return cardiBuddy.hasFlashcard(flashcard);
    }

    @Override
    public void deleteFlashcard(Flashcard target) {
        cardiBuddy.removeFlashcard(target);
    }

    /**
     * Adds Flashcard to a Deck.
     *
     * @param flashcard new card.
     */
    @Override
    public void addFlashcard(Flashcard flashcard) {
        cardiBuddy.addFlashcard(flashcard);
        updateFilteredFlashcardList(PREDICATE_SHOW_ALL_FLASHCARDS);
    }

    @Override
    public void setFlashcard(Flashcard target, Flashcard editedFlashcard) {
        requireAllNonNull(target, editedFlashcard);

        cardiBuddy.setFlashcard(target, editedFlashcard);
    }

    /**
     * Checks if the current {@code TestSession} is complete
     */
    @Override
    public boolean isTestComplete() {
        return testSession.isComplete();
    }

    /**
     * Starts a test session // TODO see how to update the list
     *
     * @param deck the deck to be tested
     */
    @Override
    public Question testDeck(Deck deck) throws EmptyDeckException {
        requireNonNull(deck);
        testSession = new TestSession(deck);
        return testSession.getFirstQuestion();
    }

    /**
     * Gets the first question from the newly created {@code TestSession}.
     */
    public Question getFirstQuestion() {
        return testSession.getFirstQuestion();
    }

    /**
     * Gets the next question in the {@code TestSession}
     */
    @Override
    public Question getNextQuestion() throws UnansweredQuestionException, NoOngoingTestException {
        try {
            return testSession.getNextQuestion();
        } catch (NullPointerException e) {
            throw new NoOngoingTestException();
        }
    }

    /**
     * Checks the given answer in the test session
     *
     * @param userAnswer a string representation of the user's answer
     * @returns A Result enums that represents the result of the user's answer.
     */
    @Override
    public TestResult submitAnswer(String userAnswer) {
        return testSession.submitAnswer(userAnswer);
    }

    /**
     * Marks the user's answer as correct when it was marked wrong by the {@code TestSession}.
     * Allows for flexibility in the user's answers.
     */
    @Override
    public void forceCorrect() throws UnansweredQuestionException, AlreadyCorrectException {
        testSession.forceCorrect();
    }

    /**
     * Clears the current {@code TestSession}.
     * Called when the test session has ended, either when there are no more flashcards
     * to test or when the user calls quit.
     */
    @Override
    public void clearTestSession() {
        if (testSession == null) { // if there is no test session to clear
            throw new NoOngoingTestException();
        }
        testSession = null;
    }

    //=========== Filtered Flashcard List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Deck} backed by the internal list of
     * {@code versionedCardiBuddy}
     */
    @Override
    public ObservableList<Deck> getFilteredDeckList() {
        return filteredDecks;
    }

    /**
     * Returns an unmodifiable view of the list of {@code Flashcard} backed by the internal list of
     * {@code versionedCardiBuddy}
     */
    @Override
    public ObservableList<Flashcard> getFilteredFlashcardList() {
        return filteredFlashcards;
    }

    @Override
    public void updateFilteredDeckList(Predicate<Deck> predicate) {
        requireNonNull(predicate);
        filteredDecks.setPredicate(predicate);
    }

    @Override
    public void updateFilteredFlashcardList(Predicate<Flashcard> predicate) {
        requireNonNull(predicate);
        filteredFlashcards.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof cardibuddy.model.ModelManager)) {
            return false;
        }

        // state check
        cardibuddy.model.ModelManager other = (cardibuddy.model.ModelManager) obj;
        return cardiBuddy.equals(other.cardiBuddy)
                && userPrefs.equals(other.userPrefs)
                && filteredDecks.equals(other.filteredDecks)
                && filteredFlashcards.equals(other.filteredFlashcards);
    }

}