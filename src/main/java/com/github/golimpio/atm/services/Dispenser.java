package com.github.golimpio.atm.services;

import java.util.concurrent.atomic.AtomicLong;

public class Dispenser {

    private AtomicLong numberOfNotes = new AtomicLong(0L);
    private Dispenser next = null;

    public void handle(long value) {

    }

    public void setNextHandle(Dispenser dispenser) {
        next = dispenser;
    }

    public Dispenser next() {
        return this;
    }

    public long getNumberOfNotes() {
        return numberOfNotes.get();
    }

    public long addNotes(long quantity) {
        return numberOfNotes.addAndGet(quantity);
    }
}
