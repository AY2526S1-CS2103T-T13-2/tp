package seedu.address.logic.grammars.command.lexer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import seedu.address.logic.grammars.command.Command;

class CommandLexerHyphenTest {

    @Test
    void dashedOptionName_supported() throws Exception {
        Command c = Command.parse("field 1 /asset-class:gold");
        assertEquals("gold", c.getOptionValue("asset-class"));
    }

    @Test
    void spacesAroundColon_allowed() throws Exception {
        Command c = Command.parse("field 1 /asset-class : gold");
        assertEquals("gold", c.getOptionValue("asset-class"));
    }
}

