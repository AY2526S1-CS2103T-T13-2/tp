package seedu.address.model.person;

import java.util.Objects;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's email in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidEmail(String)}
 */
public class Email {

    private static final String SPECIAL_CHARACTERS = "+_.-";
    
    public static final String MESSAGE_CONSTRAINTS =
            "Emails should be of the format local-part@domain and adhere to these rules:\n"
          + "1) local-part may contain alphanumeric and the following specials: ! # $ % & ' * + / = ? ` { | } ~ ^ . _ -\n"
          + "2) domain consists of labels separated by '.', each label contains alphanumeric or '-', "
          + "   and each label starts/ends with an alphanumeric character.";
 
    // alphanumeric and special characters
    private static final String ALPHANUMERIC_NO_UNDERSCORE = "[^\\W_]+"; // alphanumeric characters except underscore
    
    private static final String LOCAL_PART_REGEX = "^" + ALPHANUMERIC_NO_UNDERSCORE + "([" + SPECIAL_CHARACTERS + "]"
            + ALPHANUMERIC_NO_UNDERSCORE + ")*";
   
    private static final String DOMAIN_PART_REGEX = ALPHANUMERIC_NO_UNDERSCORE
            + "(-" + ALPHANUMERIC_NO_UNDERSCORE + ")*";
   
    private static final String DOMAIN_LAST_PART_REGEX = "(" + DOMAIN_PART_REGEX + "){2,}$"; // At least two chars
   
    private static final String DOMAIN_REGEX = "(" + DOMAIN_PART_REGEX + "\\.)*" + DOMAIN_LAST_PART_REGEX;
    
    private static final String VALIDATION_REGEX =
            "^[A-Za-z0-9!#$%&'*+/=?`{|}~^._-]+@"
          + "(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+"
          + "[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?$";    

    public final String value;

    /**
     * Constructs an {@code Email}.
     *
     * @param email A valid email address.
     */
    public Email(String value) {
        Objects.requireNonNull(value);
        if (!isValidEmail(value)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        this.value = value;
     }

    /**
     * Returns if a given string is a valid email.
     */
    public static boolean isValidEmail(String value) {
        Objects.requireNonNull(value);
        String s = value.trim(); 
      
        // no leading/trailing spaces
        if (!s.equals(value)) {
            return false;
        }
        
        // exactly one '@'
        int at = s.indexOf('@');
        if (at <= 0 || at != s.lastIndexOf('@') || at == s.length() - 1) {
            return false;
        }
   
        String local = s.substring(0, at);
        String domain = s.substring(at + 1);

        if (!isValidLocal(local)) {
            return false;
        }

        return isValidDomain(domain);
    }
    
    private static boolean isValidLocal(String local) {
        // cannot start/end with hyphen; cannot contain spaces; no consecutive dots
        if (local.isEmpty() || local.startsWith("-") || local.endsWith("-")) {
            return false;
        }
        if (local.contains(" ") || local.contains("..")) {
            return false;
        }
        // allowed chars in local: letters, digits, dot, underscore, plus, hyphen
        return local.matches("[A-Za-z0-9][A-Za-z0-9._+\\-]*");
    }

    private static boolean isValidDomain(String domain) {
        if (domain.isEmpty() || domain.contains(" ")) {
            return false;
        }
        if (domain.contains("_")) {
            return false; // underscore not allowed in domain
        }
        if (domain.startsWith(".") || domain.endsWith(".")) {
             return false;
        }
        // allow single-label domains like 'localhost' or '159'
        if (!domain.contains(".")) {
            return domain.matches("[A-Za-z0-9]+");
        }

        // multi-label domain: each label 1+ chars, no leading/trailing hyphen, only [A-Za-z0-9-]
        String[] labels = domain.split("\\.");
        for (String lbl : labels) {
            if (lbl.isEmpty()) {
                 return false;                    // no empty labels (would cover "..")
            }
            if (lbl.startsWith("-") || lbl.endsWith("-")) {
                 return false;
            }
            if (!lbl.matches("[A-Za-z0-9-]+")) {
                 return false;
            }
        }

        // top-level label must be at least 2 chars (to fail example.c)
        if (labels[labels.length - 1].length() < 2) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Email)) {
            return false;
        }

        Email otherEmail = (Email) other;
        return value.equals(otherEmail.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
