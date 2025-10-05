package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Headless UI test that turns on the preview flag and verifies pills render.
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
    static void bootFx() throws Exception {
        String os = System.getProperty("os.name", "").toLowerCase();

        try {
            if (os.contains("linux")) {
                // Headless JavaFX for CI
                System.setProperty("java.awt.headless", "true");
                System.setProperty("glass.platform", "Monocle");
                System.setProperty("monocle.platform", "Headless");
                System.setProperty("prism.order", "sw"); // software rendering
                System.setProperty("prism.text", "t2k");
                System.setProperty("prism.lcdtext", "false");

                if (!Platform.isFxApplicationThread()) {
                    final java.util.concurrent.CountDownLatch started =
                            new java.util.concurrent.CountDownLatch(1);
                    try {
                        Platform.startup(started::countDown); // ← headless FX startup
                    } catch (IllegalStateException already) {
                        // FX already started — fine
                        started.countDown();
                    }
                    if (!started.await(10, java.util.concurrent.TimeUnit.SECONDS)) {
                        org.junit.jupiter.api.Assumptions.assumeTrue(
                                false, "Skip: JavaFX startup timed out on this runner");
                    }
                }
            } else {
                // macOS/Windows: easiest and reliable — initializes JavaFX toolkit via AWT
                new javafx.embed.swing.JFXPanel();
            }
        } catch (Throwable t) {
            // If anything goes wrong, SKIP the test so CI doesn’t fail on environment issues
            org.junit.jupiter.api.Assumptions.assumeTrue(false,
                "Skip: cannot initialize JavaFX in this environment: " + t);
        }
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

        final CountDownLatch built = new CountDownLatch(1);
        final Parent[] rootRef = new Parent[1];

        Platform.runLater(() -> {
            PersonCard card = new PersonCard(DUMMY_PERSON, 1);
            rootRef[0] = card.getRoot();
            built.countDown();
        });

        assertTrue(built.await(5, TimeUnit.SECONDS), "FX init timed out");
        Parent root = rootRef[0];
        assertNotNull(root, "PersonCard root should not be null");

        VBox box = (VBox) root.lookup("#customFieldsBox");
        assertNotNull(box, "customFieldsBox should exist");
        assertTrue(box.isVisible(), "customFieldsBox should be visible when preview is enabled");
        assertFalse(box.getChildren().isEmpty(), "preview should add at least one pill");
    }

    @Test
    void personCard_noFields_hidesBox() throws Exception {
        prevPreview = System.setProperty("dev.customfields.preview", "false");

        final CountDownLatch built = new CountDownLatch(1);
        final Parent[] rootRef = new Parent[1];

        Platform.runLater(() -> {
            PersonCard card = new PersonCard(DUMMY_PERSON, 1);
            rootRef[0] = card.getRoot();
            built.countDown();
        });

        assertTrue(built.await(5, TimeUnit.SECONDS), "FX init timed out");
        VBox box = (VBox) rootRef[0].lookup("#customFieldsBox");
        assertNotNull(box, "customFieldsBox should exist");
        assertFalse(box.isVisible(), "When no fields, box should be hidden (visible=false)");
        assertFalse(box.isManaged(), "When hidden, box should be unmanaged (managed=false)");
    }

    @Test
    void personCard_previewOff_reflectionAddsSortedRows() throws Exception {
        prevPreview = System.setProperty("dev.customfields.preview", "false");

        // Subclass adds the method that the UI looks up via reflection
        Person personWithFields = new Person(
                new Name("With Fields"),
                new Phone("11111111"),
                new Email("with@fields.com"),
                new Address("1 Field St"),
                new HashSet<Tag>()) {

            @SuppressWarnings("unused")
            public java.util.Map<String, Object> getCustomFields() {
                java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
                m.put("company", "Goldman Sachs");
                m.put("asset-class", "gold");
                m.put(null, "ignored"); // null key should be ignored by tryGetCustomFields
                return m;
            }
        };

        final CountDownLatch built = new CountDownLatch(1);
        final Parent[] rootRef = new Parent[1];

        Platform.runLater(() -> {
            PersonCard card = new PersonCard(personWithFields, 1);
            rootRef[0] = card.getRoot();
            built.countDown();
        });

        assertTrue(built.await(5, TimeUnit.SECONDS), "FX init timed out");
        VBox box = (VBox) rootRef[0].lookup("#customFieldsBox");
        assertNotNull(box, "customFieldsBox should exist");

        // Box should be visible and have two rows (company, asset-class) added via kvRow(...)
        assertTrue(box.isVisible(), "Box should be visible when fields exist");
        assertFalse(box.getChildren().isEmpty(), "Rows should have been added");

        // Verify stable alphabetical order (case-insensitive): "asset-class" then "company"
        // Each child is the HBox returned by kvRow(...) (outer HBox containing the 'pill' HBox)
        javafx.scene.layout.HBox row0 = (javafx.scene.layout.HBox) box.getChildren().get(0);
        javafx.scene.layout.HBox pill0 = (javafx.scene.layout.HBox) row0.getChildren().get(0);
        String key0 = ((javafx.scene.control.Label) pill0.getChildren().get(0)).getText(); // "asset-class:"
        assertTrue(key0.toLowerCase().startsWith("asset-class"));

        javafx.scene.layout.HBox row1 = (javafx.scene.layout.HBox) box.getChildren().get(1);
        javafx.scene.layout.HBox pill1 = (javafx.scene.layout.HBox) row1.getChildren().get(0);
        String key1 = ((javafx.scene.control.Label) pill1.getChildren().get(0)).getText(); // "company:"
        assertTrue(key1.toLowerCase().startsWith("company"));
    }
}

