package com.github.golimpio.atm.services;

import com.github.golimpio.atm.model.Cash;
import com.google.common.util.concurrent.AtomicLongMap;

import java.util.*;

import static com.google.common.collect.Lists.*;

public enum CashBox {
    INSTANCE;
    public static CashBox getCashBox() { return INSTANCE; }

    private final AtomicLongMap<Cash.Note> box = AtomicLongMap.create();

    private CashBox() {
        initialise();
    }

    public synchronized void initialise() {
        box.clear();
        for (Cash.Note note : Cash.Note.values()) {
            box.put(note, 0);
        }
    }

    public synchronized void add(List<Cash> money) {
        for (Cash cash: money) {
            box.addAndGet(cash.getNote(), cash.getQuantity());
        }
    }

    public synchronized List<Cash> getMoney() {
        ArrayList<Cash> money = new ArrayList<>();
        for (Map.Entry<Cash.Note, Long> entry: box.asMap().entrySet()) {
            if (entry.getValue() > 0)
                money.add(new Cash(entry.getKey(), entry.getValue()));
        }
        return money;
    }

    public synchronized List<Cash> withdraw(long value) {
        return null;
    }

    public synchronized List<Integer> getMultiples() {
        ArrayList<Integer> multiples = new ArrayList<>();
        for (Map.Entry<Cash.Note, Long> entry: box.asMap().entrySet()) {
            if (entry.getValue() > 0) {
                int noteValue = entry.getKey().getValue();
                if (multiples.isEmpty()) {
                    multiples.add(noteValue);
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

    public long getMinimalWithdrawValue() {
        for (Map.Entry<Cash.Note, Long> entry: box.asMap().entrySet()) {
            if (entry.getValue() > 0)
                return entry.getKey().getValue();
        }
        return 0;
    }

    public synchronized boolean hasEnoughCashFor(long value) {
        return true;
    }

    public synchronized long sumInCash() {
        long sum = 0;
        for (Cash.Note note : Cash.Note.values()) {
            sum += box.get(note) * note.getValue();
        }
        return sum;
    }
}
