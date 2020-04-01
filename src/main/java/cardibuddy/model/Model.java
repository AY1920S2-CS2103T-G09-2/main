package cardibuddy.model;

import java.nio.file.Path;
import java.util.function.Predicate;

import cardibuddy.commons.core.GuiSettings;
import cardibuddy.model.deck.Deck;
import cardibuddy.model.flashcard.Flashcard;
import cardibuddy.model.flashcard.Question;
import cardibuddy.model.testsession.TestResult;
import cardibuddy.model.testsession.exceptions.EmptyDeckException;
import javafx.collections.ObservableList;

/**
 * The API of the Model component.
 */
public interface Model {
    /**
     * {@code Predicate} that always evaluate to true
     */
    Predicate<Deck> PREDICATE_SHOW_ALL_DECKS = unused -> true;

    /**
     * {@code Predicate} that always evaluate to true
     */
    Predicate<Flashcard> PREDICATE_SHOW_ALL_FLASHCARDS = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' cardibuddy file path.
     */
    Path getCardiBuddyFilePath();

    /**
     * Sets the user prefs' cardibuddy file path.
     */
    void setCardiBuddyFilePath(Path cardiBuddyFilePath);

    /**
     * Returns the CardiBuddy
     */
    ReadOnlyCardiBuddy getCardiBuddy();

    /**
     * Replaces cardibuddy data with the data in {@code cardiBuddy}.
     */
    void setCardiBuddy(ReadOnlyCardiBuddy cardiBuddy);

    /**
     * Returns true if a deck with the same identity as {@code deck} exists in the cardibuddy.
     */
    boolean hasDeck(Deck deck);

    /**
     * Deletes the given deck.
     * {@code deck} must exist in the cardibuddy.
     */
    void deleteDeck(Deck target);

    /**
     * Adds the given deck.
     * {@code deck} must not already exist in the cardibuddy.
     */
    void addDeck(Deck deck);

    /**
     * Adds the given deck.
     * {@code deck} must not already exist in the cardibuddy.
     */

    /**
     * Replaces the given deck {@code target} with {@code editedDeck}.
     * {@code target} must exist in the cardibuddy.
     * The deck identity of {@code editedDeck} must not be the same as another existing deck in the cardibuddy.
     */
    void setDeck(Deck target, Deck editedDeck);

    /**
     * Returns true if a flashcard with the same identity as {@code flashcard} exists in the cardibuddy.
     */
    boolean hasFlashcard(Flashcard flashcard);

    /**
     * Deletes the given flashcard.
     * The flashcard must exist in the cardibuddy.
     */
    void deleteFlashcard(Flashcard target);

    /**
     * Adds the given flashcard.
     * {@code flashcard} must not already exist in the cardibuddy.
     */
    void addFlashcard(Flashcard flashcard);

    /**
     * Replaces the given flashcard {@code target} with {@code editedFlashcard}.
     * {@code target} must exist in the cardibuddy.
     * The flashcard identity of {@code editedFlashcard} must not
     * be the same as another existing flashcard in the cardibuddy.
     */
    void setFlashcard(Flashcard target, Flashcard editedFlashcard);

    /**
     * Checks if the current {@code TestSession} is complete
     */
    boolean isTestComplete();

    /**
     * Starts the test session with {@code deck}
     *
     * @param deck the deck to be tested
     */
    Question testDeck(Deck deck) throws EmptyDeckException;

    /**
     * Checks the given {@code Answer} with the current flashcard's answer in the TestSession.
     */
    TestResult submitAnswer(String userAnswer);

    /**
     * Marks the user's answer as correct when it was marked wrong by the {@code TestSession}
     * Allows for flexibility in the user's answers.
     */
    void forceCorrect();

    /**
     * Gets the first question from the newly created {@code TestSession}.
     */
    Question getFirstQuestion();

    /**
     * Gets the next question in the {@code TestSession}
     */
    Question getNextQuestion();

    /**
     * Clears the current {@code TestSession}.
     * Called when the test session has ended, either when there are no more flashcards
     * to test or when the user calls quit.
     */
    void clearTestSession();

    /**
     * Returns an unmodifiable view of the filtered deck list
     */
    ObservableList<Deck> getFilteredDeckList();

    /**
     * Returns an unmodifiable view of the filtered flashcard list
     */
    ObservableList<Flashcard> getFilteredFlashcardList();

    /**
     * Updates the filter of the filtered deck list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredDeckList(Predicate<Deck> predicate);

    /**
     * Updates the filter of the filtered flashcard list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredFlashcardList(Predicate<Flashcard> predicate);
}
