package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import seedu.address.model.person.Remark;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RemarkCommand;
import seedu.address.commons.core.index.Index;

public class RemarkCommandParserTest {

    private final RemarkCommandParser parser = new RemarkCommandParser();
    Remark nonEmptyRemark = new Remark("Some remark");

    @Test
    public void parse_indexSpecified_success() {
        // have remark
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + " " + PREFIX_REMARK + nonEmptyRemark;
        Remark nonEmptyRemark = new Remark("Some remark");
        RemarkCommand expected = new RemarkCommand(targetIndex, nonEmptyRemark);
        assertParseSuccess(parser, userInput, expected);

        // no remark (interpreted as empty string)
        userInput = targetIndex.getOneBased() + " " + PREFIX_REMARK;
        expected = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(""));
        assertParseSuccess(parser, userInput, expected);
    }

    @Test
    public void parse_missingCompulsoryField_failure() {
        // NOTE: our RemarkCommandParser uses a literal error message (no Messages class)
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);

        // no parameters at all
        assertParseFailure(parser, "", expectedMessage);

        // no index (only remark prefix/value)
        Remark nonEmptyRemark = new Remark("Some remark");
        assertParseFailure(parser, " " + PREFIX_REMARK + nonEmptyRemark, expectedMessage);
    }
}

