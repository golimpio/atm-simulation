package com.github.golimpio.atm.services;

import com.github.golimpio.atm.model.Cash;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.golimpio.atm.model.Cash.Note;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DispenserTest {

    @Test
    public void handle_shouldReturnTheCorrectBalance() throws AtmException {
        Dispenser dispenser = new Dispenser(Note.FIFTY);
        dispenser.addNotes(4);
        assertThat(dispenser.getNumberOfNotes()).isEqualTo(4);

        long balance = dispenser.handle(120);

        assertThat(balance).isEqualTo(20);
    }

    @Test
    public void handle_willNotUseNotes_whenValueIsSmallerThanNote() throws AtmException {
        Dispenser dispenser = new Dispenser(Note.FIFTY);
        dispenser.addNotes(2);

        long balance = dispenser.handle(40);

        assertThat(balance).isEqualTo(40);
    }

    @Test
    public void handle_shouldCallTheNextHandleInChain() throws AtmException {
        Dispenser next = mock(Dispenser.class);
        Dispenser dispenser = new Dispenser(Note.TWENTY);

        dispenser.setNextHandle(next);
        dispenser.addNotes(2);
        dispenser.handle(30);

        verify(next).handle(10);
    }

    @Test
    public void dispense_shouldUpdateTheNumberOfNotes() throws AtmException {
        Dispenser dispenser = new Dispenser(Note.FIFTY);
        dispenser.addNotes(3);
        assertThat(dispenser.getNumberOfNotes()).isEqualTo(3);

        dispenser.handle(120);
        List<Cash> moneyDispensed = dispenser.dispense();

        assertThat(dispenser.getNumberOfNotes()).isEqualTo(1);
        assertThat(moneyDispensed).contains(new Cash(Note.FIFTY, 2), new Cash(Note.TWENTY, 1));
    }

    @Test
    public void dispense_shouldCallTheNextInChain() throws AtmException {
        Dispenser next = mock(Dispenser.class);
        Dispenser dispenser = new Dispenser(Note.FIFTY);

        dispenser.setNextHandle(next);
        dispenser.addNotes(3);
        assertThat(dispenser.getNumberOfNotes()).isEqualTo(3);

        dispenser.handle(120);
        dispenser.dispense();

        verify(next).dispense();
    }
}
