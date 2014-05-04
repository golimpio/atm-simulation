package com.github.golimpio.atm.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Cash {

    public enum Note {
        FIVE(5), TEN(10), TWENTY(20), FIFTY(50), HUNDRED(100);

        private final int value;

        Note(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private Note note;
    private long quantity;

    public Cash(Note note) {
        this(note, 0);
    }

    public Cash(Note note, long quantity) {
        setNote(note);
        setQuantity(quantity);
    }

    public Note getNote() {
        return note;
    }

    public Cash setNote(Note note) {
        this.note = note;
        return this;
    }

    public long getQuantity() {
        return quantity;
    }

    public Cash setQuantity(long quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cash)) return false;

        Cash cash = (Cash) o;

        return quantity == cash.quantity && note == cash.note;
    }

    @Override
    public int hashCode() {
        int result = note.hashCode();
        result = 31 * result + (int) (quantity ^ (quantity >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Cash{" +
                "note=" + note +
                ", quantity=" + quantity +
                '}';
    }
}
