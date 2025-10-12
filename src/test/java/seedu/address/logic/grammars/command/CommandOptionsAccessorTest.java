package seedu.address.logic.grammars.command;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

class CommandOptionsAccessorTest {

    @Test
    void getAllOptions_containsDashedKey_unmodifiable() throws Exception {
        Command c = Command.parse("field 1 /asset-class:gold /company:GS");
        Map<String,String> opts = c.getAllOptions();

        assertEquals("gold", opts.get("asset-class"));
        assertThrows(UnsupportedOperationException.class, () -> opts.put("x","y"));
    }
}

