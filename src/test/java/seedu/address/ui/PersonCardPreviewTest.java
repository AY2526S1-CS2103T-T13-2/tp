package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel; // <-- starts the JavaFX runtime in headless tests
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Minimal headless test that turns on the preview flag and asserts that the
 * custom-fields section is visible and non-empty on PersonCard.
 */
public class PersonCardPreviewTest {

    private static final Person DUMMY_PERSON = new Person(
            new Name("Preview Person"),
            new Phone("99999999"),
            new Email("preview@example.com"),
            new Address("123 Preview Street"),
            new HashSet<Tag>());

    private String prevPreview;

    @BeforeAll
    static void bootJavaFx() throws Exception {
        // Ensure JavaFX toolkit is initialized (headless-safe)
        // Either of these will work; JFXPanel is the most reliable on CI.
        SwingUtilities.invokeAndWait(JFXPanel::new);
    }

    @AfterEach
    void restorePreviewFlag() {
        if (prevPreview == null) {
            System.clearProperty("dev.customfields.preview");
        } else {
            System.setProperty("dev.customfields.preview", prevPreview);
        }
    }

    @Test
    void personCard_previewOn_customFieldsVisible() throws Exception {
        prevPreview = System.setProperty("dev.customfields.preview", "true");

        CountDownLatch latch = new CountDownLatch(1);
        final Parent[] rootHolder = new Parent[1];

        // Construct on the FX thread so FXMLLoader runs safely
        Platform.runLater(() -> {
            PersonCard card = new PersonCard(DUMMY_PERSON, 1);
            rootHolder[0] = card.getRoot();
            latch.countDown();
        });

        // Wait for the UI construction to complete
        assertTrue(latch.await(5, TimeUnit.SECONDS), "FX init timed out");

        Parent root = rootHolder[0];
        assertNotNull(root, "PersonCard root should not be null");

        VBox customFieldsBox = (VBox) root.lookup("#customFieldsBox");
        assertNotNull(customFieldsBox, "customFieldsBox should exist");
        assertTrue(customFieldsBox.isVisible(), "customFieldsBox should be visible when preview is enabled");
        assertFalse(customFieldsBox.getChildren().isEmpty(), "preview should add at least one pill");
    }
}

