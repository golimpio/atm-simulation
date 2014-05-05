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
    long calculate(long value) throws AtmException {
        long balance = value;
        notesToDispense = 0;

        if (value >= note.getValue()) {
            notesToDispense = value / note.getValue();
            while (notesToDispense > 0) {
                if (notesToDispense > numberOfNotes.get()) {
                    notesToDispense = numberOfNotes.get();
                }
                balance = value - notesToDispense * note.getValue();
                if (balance < 0)
                    throw new AtmException("Internal error: balance is negative for " + note);
                if (balance == 0)
                    break;

                long predeterminedBalance = predetermine(balance);
                if (predeterminedBalance == 0)
                    break;

                notesToDispense--;
                balance = value - notesToDispense * note.getValue();
            }
        }
        return (next != null) ? next.calculate(balance) : balance;
    }

    long predetermine(long value) {
        long balance = value;

        if (value >= note.getValue()) {
            long dispenseNotes = value / note.getValue();
            if (dispenseNotes > numberOfNotes.get()) {
                dispenseNotes = numberOfNotes.get();
            }
            balance = value - dispenseNotes * note.getValue();
        }
        return (next != null) ? next.predetermine(balance) : balance;
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
}
