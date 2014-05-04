package com.github.golimpio.atm.services;

import com.github.golimpio.atm.model.Cash;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum CashBox {
    INSTANCE;
    public static CashBox getCashBox() { return INSTANCE; }

    static final long MAX_NOTES = 100_000;

//    private final AtomicLongMap<Cash.Note> box = AtomicLongMap.create();

    private Dispenser firstDispenser = null;

    ConcurrentHashMap<Cash.Note, Dispenser> box = new ConcurrentHashMap<>();

    private CashBox() {
        initialise();
    }

    public synchronized void initialise() {
        box.clear();

        Dispenser previousDispenser = null;

        for (Cash.Note note : Cash.Note.values()) {
            Dispenser dispenser = new Dispenser();
            box.put(note, new Dispenser());

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
                throw new AtmException("Internal error: no dispenser was found for: " + cash.getNote());
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
        firstDispenser.handle(value);

        return null;
    }

    private void validateWithdraw(long value) throws AtmException {
        if (value <= 0)
            throw new AtmException("Value for withdraw must be bigger than zero.");
        if (firstDispenser == null)
            throw new AtmException("Internal error: dispensers were not initialised correctly.");
        if (value > availableMoney())
            throw new AtmException("There is no enough money to fulfill the request.");

        boolean isMultiple = false;
        List<Integer> multiples = multiples();
        for (int multiple: multiples) {
            if (value % multiple == 0) {
                isMultiple = true;
                break;
            }
        }
        if (!isMultiple)
            throw new AtmException("Value is not multiple of available notes.");
    }

    public synchronized List<Integer> getMultiples() {
        return multiples();
    }

    private List<Integer> multiples() {
        ArrayList<Integer> multiples = new ArrayList<>();
        for (Cash.Note note : Cash.Note.values()) {
            Dispenser dispenser = box.get(note);
            if (dispenser.getNumberOfNotes() > 0) {
                int noteValue = note.getValue();
                if (multiples.isEmpty()) {
                    multiples.add(note.getValue());
                }
                else {
                    for (int multiple : multiples) {
                        if (noteValue % multiple != 0) {
                            multiples.add(noteValue);
                            break;
                        }
                    }
                }
            }
        }
        return multiples;
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
        return true;
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
