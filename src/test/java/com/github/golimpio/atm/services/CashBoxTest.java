package com.github.golimpio.atm.services;

import com.github.golimpio.atm.model.Cash;
import com.google.common.collect.Iterables;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.golimpio.atm.model.Cash.Note;
import static com.github.golimpio.atm.services.CashBox.cashBox;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class CashBoxTest {

    @Test
    public void initialise_shouldZeroCashForAllNotes() {
        cashBox().initialise();
        assertThat(cashBox().getMoney()).isEmpty();
        assertThat(cashBox().sumInCash()).isZero();
    }

    @Test
    public void add_shouldAddMoneyToCashBox_whenBoxIsEmpty() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, 10), cash(Note.FIFTY, 20));

        cashBox().initialise();
        cashBox().add(money);

        assertThat(cashBox().getMoney()).containsAll(money);
        assertThat(cashBox().sumInCash()).isEqualTo(20 * 10 + 50 * 20);
    }

    @Test
    public void add_shouldAddMoneyToCashBox_whenBoxIsNotEmpty() throws AtmException {
        ArrayList<Cash> moneyOne = newArrayList(cash(Note.TWENTY, 10), cash(Note.FIFTY, 20));
        ArrayList<Cash> moneyTwo = newArrayList(cash(Note.FIVE, 30), cash(Note.HUNDRED, 5));

        cashBox().initialise();
        cashBox().add(moneyOne);
        cashBox().add(moneyTwo);

        assertThat(cashBox().getMoney()).containsAll(Iterables.concat(moneyOne, moneyTwo));
        assertThat(cashBox().sumInCash()).isEqualTo(20 * 10 + 50 * 20 + 5 * 30 + 100 * 5);
    }

    @Test(expectedExceptions = AtmException.class)
    public void add_shouldThrowException_whenQuantityIsNegative() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, 5), cash(Note.FIFTY, -1));

        cashBox().initialise();
        cashBox().add(money);

        fail("Adding notes with negative quantity should have failed!");
    }

    @Test(expectedExceptions = AtmException.class)
    public void add_shouldThrowException_whenQuantityIsZero() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, 0), cash(Note.FIFTY, 10));

        cashBox().initialise();
        cashBox().add(money);

        fail("Adding notes with quantity equals to zero should have failed!");
    }

    @Test(expectedExceptions = AtmException.class)
    public void add_shouldThrowException_whenQuantityIsBiggerThanMaxAllowed() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, CashBox.MAX_NOTES + 1), cash(Note.FIFTY, 10));

        cashBox().initialise();
        cashBox().add(money);

        fail("Adding notes with quantity equals to zero should have failed!");
    }

    @Test
    public void getMinimalWithdrawValue_shouldBeZero_whenThereIsNoMoney() {
        cashBox().initialise();
        assertThat(cashBox().sumInCash()).isZero();
        assertThat(cashBox().getMinimalWithdrawValue()).isZero();
    }

    @Test
    public void getMinimalWithdrawValue_shouldBeUpdated_afterEachLastWithdraw() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, 4), cash(Note.FIFTY, 1));

        cashBox().initialise();
        cashBox().add(money);
        assertThat(cashBox().getMinimalWithdrawValue()).isEqualTo(20);

        cashBox().withdraw(80);
        assertThat(cashBox().getMinimalWithdrawValue()).isEqualTo(50);
    }

    @Test
    public void hasEnoughCashFor_shouldReturnTrue_whenMoneyInTheBoxIsEnough() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, 10), cash(Note.FIFTY, 20));

        cashBox().initialise();
        cashBox().add(money);

        assertThat(cashBox().hasEnoughCashFor(400)).isTrue();
    }

    @Test
    public void hasEnoughCashFor_shouldReturnFalse_whenMoneyInTheBoxIsNotEnough() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, 1), cash(Note.FIFTY, 1));

        cashBox().initialise();
        cashBox().add(money);

        assertThat(cashBox().hasEnoughCashFor(200)).isFalse();
    }

    @Test(expectedExceptions = AtmException.class)
    public void withdraw_shouldThrowException_whenValueIsZero() throws AtmException {
        cashBox().withdraw(0);
        fail("Withdraw a value equals to zero should have failed!");
    }

    @Test(expectedExceptions = AtmException.class)
    public void withdraw_shouldThrowException_whenThereIsNoEnoughMoney() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, 4), cash(Note.FIFTY, 1));

        cashBox().initialise();
        cashBox().add(money);
        assertThat(cashBox().sumInCash()).isEqualTo(130);

        cashBox().withdraw(150);
        fail("Withdraw when there is no enough money should have failed!");
    }

    @Test(expectedExceptions = AtmException.class)
    public void withdraw_shouldThrowException_whenValueIsNotMultipleOfAvailableNotes() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.FIVE, 20), cash(Note.FIFTY, 20));

        cashBox().initialise();
        cashBox().add(money);

        cashBox().withdraw(33);
        fail("Withdraw a value that is not multiple of available notes should have failed!");
    }

    @Test
    public void withdraw_shouldUpdateAmountOfNotes() throws AtmException {
        ArrayList<Cash> money = newArrayList(cash(Note.TWENTY, 10), cash(Note.FIFTY, 20));

        cashBox().initialise();
        cashBox().add(money);
        assertThat(cashBox().sumInCash()).isEqualTo(10 * 20 + 50 * 20);

        cashBox().withdraw(90);
        assertThat(cashBox().sumInCash()).isEqualTo(10 * 20 + 50 * 20 - 90);
    }

    @DataProvider
    private static Object[][] providerForWithdraw() {
        return new Object[][] {
                new Object[] {80,
                        new Cash[] {cash(Note.TWENTY, 10), cash(Note.FIFTY, 5)},
                        new Cash[] {cash(Note.TWENTY, 4)}},
                new Object[] {90,
                        new Cash[] {cash(Note.TWENTY, 10), cash(Note.FIFTY, 5)},
                        new Cash[] {cash(Note.FIFTY, 1), cash(Note.TWENTY, 2)}},
                new Object[] {100,
                        new Cash[] {cash(Note.TWENTY, 10), cash(Note.FIFTY, 5)},
                        new Cash[] {cash(Note.FIFTY, 2)}},
                new Object[] {1000,
                        new Cash[] {cash(Note.HUNDRED, 5), cash(Note.FIFTY, 50)},
                        new Cash[] {cash(Note.HUNDRED, 5), cash(Note.FIFTY, 10)}},
                new Object[] {35,
                        new Cash[] {cash(Note.FIVE, 1), cash(Note.TWENTY, 1), cash(Note.TEN, 35)},
                        new Cash[] {cash(Note.TWENTY, 1), cash(Note.TEN, 1), cash(Note.FIVE, 1)}},
        };
    }

    @Test(dataProvider = "providerForWithdraw")
    public void withdraw(long value, Cash[] cashArray, Cash[] expected) throws AtmException {
        ArrayList<Cash> money = newArrayList(cashArray);

        cashBox().initialise();
        cashBox().add(money);

        List<Cash> dispensedMoney = cashBox().withdraw(value);
        assertThat(dispensedMoney).containsAll(newArrayList(expected));
    }

    private static Cash cash(Note note, long quantity) {
        return new Cash(note, quantity);
    }
}
