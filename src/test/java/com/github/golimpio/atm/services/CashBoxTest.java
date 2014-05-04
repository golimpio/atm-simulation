package com.github.golimpio.atm.services;

import com.github.golimpio.atm.model.Cash;
import com.google.common.collect.Iterables;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static com.github.golimpio.atm.model.Cash.Note;
import static com.github.golimpio.atm.services.CashBox.getCashBox;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Fail.fail;

import static com.google.common.collect.Lists.*;

public class CashBoxTest {

//    private static final ArrayList<Cash> NO_MONEY = new ArrayList<Cash>() {{
//        for (Cash.Note note : Cash.Note.values())
//            add(new Cash(note));
//    }};

    @Test
    public void initialise_shouldZeroCashForAllNotes() {
        getCashBox().initialise();
        assertThat(getCashBox().getMoney()).isEmpty();
        assertThat(getCashBox().sumInCash()).isZero();
    }

    @Test
    public void add_shouldAddMoneyToCashBox_whenBoxIsEmpty() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, 10), cash(Note.FIFTY, 20));

        getCashBox().initialise();
        getCashBox().add(money);

        assertThat(getCashBox().getMoney()).containsAll(money);
        assertThat(getCashBox().sumInCash()).isEqualTo(20 * 10 + 50 * 20);
    }

    @Test
    public void add_shouldAddMoneyToCashBox_whenBoxIsNotEmpty() throws AtmException {
        ArrayList<Cash> moneyOne = newArrayList(cash(Note.TWENTY, 10), cash(Note.FIFTY, 20));
        ArrayList<Cash> moneyTwo = newArrayList(cash(Note.FIVE, 30), cash(Note.HUNDRED, 5));

        getCashBox().initialise();
        getCashBox().add(moneyOne);
        getCashBox().add(moneyTwo);

        assertThat(getCashBox().getMoney()).containsAll(Iterables.concat(moneyOne, moneyTwo));
        assertThat(getCashBox().sumInCash()).isEqualTo(20 * 10 + 50 * 20 + 5 * 30 + 100 * 5);
    }

    @Test(expectedExceptions = AtmException.class)
    public void add_shouldThrowException_whenQuantityIsNegative() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, 5), cash(Note.FIFTY, -1));

        getCashBox().initialise();
        getCashBox().add(money);

        fail("Adding notes with negative quantity should have failed!");
    }

    @Test(expectedExceptions = AtmException.class)
    public void add_shouldThrowException_whenQuantityIsZero() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, 0), cash(Note.FIFTY, 10));

        getCashBox().initialise();
        getCashBox().add(money);

        fail("Adding notes with quantity equals to zero should have failed!");
    }

    @Test(expectedExceptions = AtmException.class)
    public void add_shouldThrowException_whenQuantityIsBiggerThanMaxAllowed() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, CashBox.MAX_NOTES + 1), cash(Note.FIFTY, 10));

        getCashBox().initialise();
        getCashBox().add(money);

        fail("Adding notes with quantity equals to zero should have failed!");
    }

    @Test
    public void getMinimalWithdrawValue_shouldBeZero_whenThereIsNoMoney() {
        getCashBox().initialise();
        assertThat(getCashBox().sumInCash()).isZero();
        assertThat(getCashBox().getMinimalWithdrawValue()).isZero();
    }

    @Test
    public void getMinimalWithdrawValue_shouldBeUpdated_afterLastWithdraw() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, 4), cash(Note.FIFTY, 1));

        getCashBox().initialise();
        getCashBox().add(money);
        assertThat(getCashBox().getMinimalWithdrawValue()).isEqualTo(20);

        getCashBox().withdraw(80);
        assertThat(getCashBox().getMinimalWithdrawValue()).isEqualTo(50);
    }

    @Test
    public void getMultiples() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, 10), cash(Note.FIFTY, 20));

        getCashBox().initialise();
        getCashBox().add(money);

        assertThat(getCashBox().getMultiples()).contains(20, 50);
    }

    @Test(expectedExceptions = AtmException.class)
    public void withdraw_shouldThrowException_whenValueIsZero() throws AtmException {
        getCashBox().withdraw(0);
        fail("Withdraw a value equals to zero should have failed!");
    }

    @Test(expectedExceptions = AtmException.class)
    public void withdraw_shouldThrowException_whenThereIsNoEnoughMoney() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, 4), cash(Note.FIFTY, 1));

        getCashBox().initialise();
        getCashBox().add(money);
        assertThat(getCashBox().sumInCash()).isEqualTo(130);

        getCashBox().withdraw(150);
        fail("Withdraw when there is no enough money should have failed!");
    }

    @Test(expectedExceptions = AtmException.class)
    public void withdraw_shouldThrowException_whenValueIsNotMultipleOfAvailableNotes() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, 10), cash(Note.FIFTY, 20));

        getCashBox().initialise();
        getCashBox().add(money);
        assertThat(getCashBox().getMultiples()).contains(20, 50);

        getCashBox().withdraw(30);
        fail("Withdraw a value that is not multiple of available notes should have failed!");
    }

    private Cash cash(Note note, long quantity) {
        return new Cash(note, quantity);
    }
}
