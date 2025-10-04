package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Value object representing a person's remark.
 * Immutable; may be an empty string to represent no remark.
 */
public final class Remark {
    public final String value;

    /**
    * Constructs a {@code Remark}.
    *
    * @param remark Non-null remark string (may be empty).
    */
    public Remark(String value) {
        requireNonNull(value);
        this.value = value;
    }

    public boolean isEmpty() {
        return value.isBlank();
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
        if (!(other instanceof Remark)) {
            return false;
        }
        Remark r = (Remark) other;
        return value.equals(r.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

