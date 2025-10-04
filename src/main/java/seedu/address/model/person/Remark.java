package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

public final class Remark {
    public final String value;
    public Remark(String value) { requireNonNull(value); this.value = value; }
    public boolean isEmpty() { return value.isBlank(); }
    @Override public String toString() { return value; }
    @Override public boolean equals(Object o){ return o instanceof Remark r && value.equals(r.value); }
    @Override public int hashCode(){ return value.hashCode(); }
}

