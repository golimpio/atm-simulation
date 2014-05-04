package com.github.golimpio.atm.services;

import com.github.golimpio.atm.model.Cash;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

class Dispenser {

    private AtomicLong numberOfNotes = new AtomicLong(0L);
    private Cash.Note note;
    private Dispenser next = null;
    private long notesToDispense = 0;

    Dispenser(Cash.Note note) {
        this.note = note;
    }

    /**
     * Calculate the number of notes to dispense for this withdraw.
     *
     * @param value The total in cash to dispense (withdraw)
     * @return The amount/balance that couldn't be dispensed
     */
    long handle(long value) throws AtmException {
        long balance = value;
        notesToDispense = 0;

        if (value >= note.getValue()) {
            notesToDispense = value / note.getValue();
            if (notesToDispense > numberOfNotes.get()) {
                notesToDispense = numberOfNotes.get();
            }
            balance = value - notesToDispense * note.getValue();
            if (balance < 0)
                throw new AtmException("Internal error: balance is negative for " + note);
        }

        return (next != null) ? next.handle(balance) : balance;
    }

    long predetermine() {
        return 0;
    }

    List<Cash> dispense() {
        List<Cash> moneyDispensed = new ArrayList<>();

        if (notesToDispense > 0) {
            moneyDispensed.add(new Cash(note, notesToDispense));
            numberOfNotes.addAndGet(notesToDispense * -1);
            notesToDispense = 0;
        }

        if (next != null)
            moneyDispensed.addAll(next.dispense());

        return moneyDispensed;
    }

    void setNextHandle(Dispenser dispenser) {
        next = dispenser;
    }

    long getNumberOfNotes() {
        return numberOfNotes.get();
    }

    long addNotes(long quantity) {
        return numberOfNotes.addAndGet(quantity);
    }
//
//    long getNotesToDispense() {
//        return notesToDispense;
//    }
}
