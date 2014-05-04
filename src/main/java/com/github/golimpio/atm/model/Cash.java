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
    private long numberOfNotes;

    public Cash(Note note) {
        this(note, 0);
    }

    public Cash(Note note, long numberOfNotes) {
        setNote(note);
        setNumberOfNotes(numberOfNotes);
    }

    public Note getNote() {
        return note;
    }

    public Cash setNote(Note note) {
        this.note = note;
        return this;
    }

    public long getNumberOfNotes() {
        return numberOfNotes;
    }

    public Cash setNumberOfNotes(long number) {
        this.numberOfNotes = number;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cash)) return false;

        Cash cash = (Cash) o;

        return numberOfNotes == cash.numberOfNotes && note == cash.note;
    }

    @Override
    public int hashCode() {
        int result = note.hashCode();
        result = 31 * result + (int) (numberOfNotes ^ (numberOfNotes >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Cash{" +
                "note=" + note +
                ", quantity=" + numberOfNotes +
                '}';
    }
}
