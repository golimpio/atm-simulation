package com.github.golimpio.atm.services;

import com.github.golimpio.atm.model.Cash;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum CashBox {
    INSTANCE;
    public static CashBox cashBox() { return INSTANCE; }

    static final long MAX_NOTES = 100_000;

    private Dispenser firstDispenser = null;

    ConcurrentHashMap<Cash.Note, Dispenser> box = new ConcurrentHashMap<>();

    private CashBox() {
        initialise();
    }

    public synchronized void initialise() {
        box.clear();

        Dispenser previousDispenser = null;

        for (int i = Cash.Note.values().length - 1; i >= 0 ; i--) {
            Cash.Note note = Cash.Note.values()[i];
            Dispenser dispenser = new Dispenser(note);
            box.put(note, dispenser);

            if (previousDispenser == null) {
                firstDispenser = dispenser;
            } else {
                previousDispenser.setNextHandle(dispenser);
            }
            previousDispenser = dispenser;
        }
    }

    public synchronized void add(List<Cash> money) throws AtmException {
        validateMoney(money);
        for (Cash cash: money) {
            Dispenser dispenser = box.get(cash.getNote());
            if (dispenser == null)
                throw new AtmException("No dispenser was found for: " + cash.getNote(), true);
            dispenser.addNotes(cash.getNumberOfNotes());
        }
    }

    private void validateMoney(List<Cash> money) throws AtmException {
        for (Cash cash: money) {
            if (cash.getNote() == null)
                throw new AtmException("An invalid note was specified [" + cash + "]");
            if (cash.getNumberOfNotes() <= 0)
                throw new AtmException("Quantity must be bigger than zero [" + cash + "]");
            if (cash.getNumberOfNotes() > MAX_NOTES)
                throw new AtmException("Quantity must be smaller or equal to " + MAX_NOTES + " [" + cash + "]");
        }
    }

    public synchronized List<Cash> getMoney() {
        ArrayList<Cash> money = new ArrayList<>();
        for (Cash.Note note : Cash.Note.values()) {
            Dispenser dispenser = box.get(note);
            if (dispenser.getNumberOfNotes() > 0)
                money.add(new Cash(note, dispenser.getNumberOfNotes()));
        }
        return money;
    }

    public synchronized List<Cash> withdraw(long value) throws AtmException {
        validateWithdraw(value);
        long balance = firstDispenser.calculate(value);
        if (balance > 0) {
            throw new AtmException("The combination of notes available didn't satisfy your request, " +
                                   "please select another amount and try it again.");
        }

        // The withdraw is done in two steps (calculate + dispense),
        // so if anything goes wrong on the first step, the number of notes will not be corrupted
        return firstDispenser.dispense();
    }

    private void validateWithdraw(long value) throws AtmException {
        if (value <= 0)
            throw new AtmException("Value for withdraw must be bigger than zero.");
        if (firstDispenser == null)
            throw new AtmException("Dispensers were not initialised correctly.", true);
        if (value > availableMoney())
            throw new AtmException("There is no enough money to fulfill the request.");
        if (!isMultiple(value))
            throw new AtmException("Value is not multiple or a combination of available notes.");
    }

    private boolean isMultiple(long value) {
        for (Cash.Note note : Cash.Note.values()) {
            if (value % note.getValue() == 0)
                return true;
        }
        return false;
    }

    public synchronized long getMinimalWithdrawValue() {
        for (Cash.Note note : Cash.Note.values()) {
            Dispenser dispenser = box.get(note);
            if (dispenser.getNumberOfNotes() > 0)
                return note.getValue();
        }
        return 0;
    }

    public synchronized boolean hasEnoughCashFor(long value) {
        return availableMoney() >= value;
    }

    public synchronized long sumInCash() {
        return availableMoney();
    }

    private long availableMoney() {
        long sum = 0;
        for (Cash.Note note : Cash.Note.values()) {
            sum += box.get(note).getNumberOfNotes() * note.getValue();
        }
        return sum;
    }
}
