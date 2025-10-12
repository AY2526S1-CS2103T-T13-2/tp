package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.grammars.command.Command;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

class FieldCommandTest {

    private Model model;

    @BeforeEach
    void setup() {
        model = new ModelManager(new AddressBook(), new UserPrefs());
        // Add one person so index 1 is valid
        Person base = new PersonBuilder().withName("Alex Yeoh").build();
        model.addPerson(base);
    }

    @Test
    void execute_singlePair_success() throws Exception {
        Command gc = Command.parse("field 1 /company:\"Goldman Sachs\"");
        FieldCommand cmd = new FieldCommand(gc);

        String feedback = cmd.execute(model);

        assertTrue(feedback.contains("company:Goldman Sachs"));
        Person edited = model.getFilteredPersonList().get(0);
        assertEquals("Goldman Sachs", edited.getCustomFields().get("company"));
    }

    @Test
    void execute_multiplePairs_overwriteAndOrder() throws Exception {
        Command gc = Command.parse("field 1 /asset-class:gold /company:\"Goldman Sachs\" /company:GS");
        FieldCommand cmd = new FieldCommand(gc);
        cmd.execute(model);

        Person edited = model.getFilteredPersonList().get(0);
        // last write wins
        assertEquals("GS", edited.getCustomFields().get("company"));
        // order preserved
        assertArrayEquals(new String[]{"asset-class", "company"},
                edited.getCustomFields().keySet().toArray(new String[0]));
    }

    @Test
    void constructor_fromGrammar_invalidIndex_throws() throws Exception {
        Command gc = Command.parse("field x /k:v");
        assertThrows(IllegalArgumentException.class, () -> new FieldCommand(gc));
    }

    @Test
    void constructor_fromGrammar_noPairs_throws() throws Exception {
        Command gc = Command.parse("field 1"); // no /k:v
        assertThrows(IllegalArgumentException.class, () -> new FieldCommand(gc));
    }

    @Test
    void execute_indexOutOfBounds_throws() throws Exception {
        // There is only 1 person in the model
        Command gc = Command.parse("field 2 /k:v");
        FieldCommand cmd = new FieldCommand(gc);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> cmd.execute(model));
        assertEquals(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, ex.getMessage());
    }
}

