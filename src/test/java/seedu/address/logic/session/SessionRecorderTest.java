package seedu.address.logic.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.person.FieldContainsKeywordsPredicate;
import seedu.address.session.SessionData;
import seedu.address.testutil.PersonBuilder;

public class SessionRecorderTest {

    private static final GuiSettings DEFAULT_GUI_SETTINGS = new GuiSettings();

    @Test
    public void markSnapshotPersisted_withoutDirtyFlag_noEffect() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        assertFalse(recorder.isAddressBookDirty(new AddressBook()));
        recorder.markSnapshotPersisted();
        assertFalse(recorder.isAddressBookDirty(new AddressBook()));
    }

    @Test
    public void markSnapshotPersisted_calledMultipleTimes_idempotent() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        recorder.afterSuccessfulCommand(new AddCommand(new PersonBuilder().build()), true);
        assertTrue(recorder.isAddressBookDirty(createSampleAddressBook()));
        recorder.markSnapshotPersisted();
        assertFalse(recorder.isAddressBookDirty(createSampleAddressBook()));
        recorder.markSnapshotPersisted();
        assertFalse(recorder.isAddressBookDirty(createSampleAddressBook()));
    }

    @Test
    public void findAndListCommand_interleaving_noPersistenceOfKeywords() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        List<String> keywords = List.of("alice", "bob");
        recorder.afterSuccessfulCommand(new FindCommand(new FieldContainsKeywordsPredicate(keywords)), false);
        // Keywords are transient and should not be included in persisted snapshots
        SessionData snapshot = recorder.buildSnapshot(new AddressBook(), DEFAULT_GUI_SETTINGS);
        assertEquals(DEFAULT_GUI_SETTINGS, snapshot.getGuiSettings());
        assertFalse(recorder.isAddressBookDirty(new AddressBook()));
        recorder.afterSuccessfulCommand(new ListCommand(), false);
        snapshot = recorder.buildSnapshot(new AddressBook(), DEFAULT_GUI_SETTINGS);
        assertEquals(DEFAULT_GUI_SETTINGS, snapshot.getGuiSettings());
        assertFalse(recorder.isAddressBookDirty(new AddressBook()));
    }


    private AddressBook createSampleAddressBook() {
        AddressBook book = new AddressBook();
        book.addPerson(new PersonBuilder().build());
        return book;
    }

    @Test
    public void constructor_withoutSession_initialisesCleanState() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        SessionData snapshot = recorder.buildSnapshot(createSampleAddressBook(), DEFAULT_GUI_SETTINGS);

        // No search keywords are persisted; snapshot should contain GUI/address book state only
        assertEquals(DEFAULT_GUI_SETTINGS, snapshot.getGuiSettings());
        assertFalse(recorder.isAddressBookDirty(createSampleAddressBook()));
    }

    @Test
    public void constructor_withExistingSession_initialisesFromGuiSettings() {
        AddressBook addressBook = createSampleAddressBook();
        SessionData previous = new SessionData(
                Instant.parse("2024-01-01T10:10:00Z"),
                addressBook,
                DEFAULT_GUI_SETTINGS);

        SessionRecorder recorder = new SessionRecorder(addressBook, DEFAULT_GUI_SETTINGS, Optional.of(previous));
        SessionData snapshot = recorder.buildSnapshot(addressBook, DEFAULT_GUI_SETTINGS);

        assertEquals(DEFAULT_GUI_SETTINGS, snapshot.getGuiSettings());
        assertFalse(recorder.isAddressBookDirty(addressBook));
    }

    @Test
    public void afterSuccessfulCommand_marksDirtyWhenAddressBookChanges() {
        AddressBook current = createSampleAddressBook();
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        recorder.afterSuccessfulCommand(new AddCommand(new PersonBuilder().build()), true);

        assertTrue(recorder.isAddressBookDirty(current));
    }

    @Test
    public void afterSuccessfulCommand_nonMutatingCommandRetainsCleanFlag() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        recorder.afterSuccessfulCommand(new ListCommand(), false);

        assertFalse(recorder.isAddressBookDirty(new AddressBook()));
    }

    @Test
    public void afterSuccessfulCommand_findDoesNotPersistKeywords() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        List<String> keywords = List.of("alice", "bob");
        recorder.afterSuccessfulCommand(new FindCommand(new FieldContainsKeywordsPredicate(keywords)), false);

        SessionData snapshot = recorder.buildSnapshot(new AddressBook(), DEFAULT_GUI_SETTINGS);
        // Keywords are not persisted; snapshot should still only contain GUI/address book state
        assertEquals(DEFAULT_GUI_SETTINGS, snapshot.getGuiSettings());
        assertFalse(recorder.isAddressBookDirty(new AddressBook()));
    }

    @Test
    public void afterSuccessfulCommand_listHasNoPersistentEffect() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        recorder.afterSuccessfulCommand(new FindCommand(new FieldContainsKeywordsPredicate(List.of("alice"))), false);

        recorder.afterSuccessfulCommand(new ListCommand(), false);

        SessionData snapshot = recorder.buildSnapshot(new AddressBook(), DEFAULT_GUI_SETTINGS);
        assertEquals(DEFAULT_GUI_SETTINGS, snapshot.getGuiSettings());
    }

    @Test
    public void metadataChange_thenRevert_notDirty() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        // only GUI settings are considered metadata for persistence
        GuiSettings updated = new GuiSettings(1024, 768, 10, 10);
        recorder.afterGuiSettingsChanged(updated);
        assertTrue(recorder.isAnyDirty(new AddressBook(), updated));

        recorder.afterGuiSettingsChanged(DEFAULT_GUI_SETTINGS);
        assertFalse(recorder.isAnyDirty(new AddressBook(), DEFAULT_GUI_SETTINGS));
    }

    @Test
    public void guiSettingsChange_onlyMarksDirtyWhenValueDiffers() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);

        GuiSettings updatedSettings = new GuiSettings(1024, 768, 10, 10);
        recorder.afterGuiSettingsChanged(updatedSettings);
        assertTrue(recorder.isAnyDirty(new AddressBook(), updatedSettings));

        // Setting the same value again should not clear the dirty flag prematurely
        recorder.afterGuiSettingsChanged(updatedSettings);
        assertTrue(recorder.isAnyDirty(new AddressBook(), updatedSettings));

        // Reverting to baseline clears the metadata dirty flag
        recorder.afterGuiSettingsChanged(DEFAULT_GUI_SETTINGS);
        assertFalse(recorder.isAnyDirty(new AddressBook(), DEFAULT_GUI_SETTINGS));
    }

    @Test
    public void markSnapshotPersisted_resetsDirtyFlag() {
        AddressBook current = createSampleAddressBook();
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        recorder.afterSuccessfulCommand(new AddCommand(new PersonBuilder().build()), true);
        assertTrue(recorder.isAddressBookDirty(current));

        recorder.markSnapshotPersisted();

        assertFalse(recorder.isAddressBookDirty(current));
    }

    @Test
    public void buildSnapshot_containsAddressBookState() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        AddressBook addressBook = createSampleAddressBook();

        SessionData snapshot = recorder.buildSnapshot(addressBook, DEFAULT_GUI_SETTINGS);

        assertEquals(addressBook.getPersonList(), snapshot.getAddressBook().getPersonList());
        assertEquals(DEFAULT_GUI_SETTINGS, snapshot.getGuiSettings());
    }

    @Test
    public void dirtyFlag_andSnapshotReflectsAddressBookMutation() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        AddressBook ab = createSampleAddressBook();
        // Simulate a mutating command
        recorder.afterSuccessfulCommand(new AddCommand(new PersonBuilder().withName("Bob").build()), true);
        assertTrue(recorder.isAddressBookDirty(ab));

        SessionData snapshot = recorder.buildSnapshot(ab, DEFAULT_GUI_SETTINGS);
        assertEquals(ab, new AddressBook(snapshot.getAddressBook()));
        recorder.markSnapshotPersisted();
        assertFalse(recorder.isAddressBookDirty(ab));
    }

    @Test
    public void sessionData_restoredDoesNotIncludeKeywords() {
        AddressBook ab = createSampleAddressBook();
        SessionData previous = new SessionData(
                Instant.parse("2024-01-01T10:10:00Z"),
                ab,
                DEFAULT_GUI_SETTINGS);
        SessionRecorder recorder = new SessionRecorder(ab, DEFAULT_GUI_SETTINGS, Optional.of(previous));
        SessionData snapshot = recorder.buildSnapshot(ab, DEFAULT_GUI_SETTINGS);
        assertEquals(DEFAULT_GUI_SETTINGS, snapshot.getGuiSettings());
    }
}

