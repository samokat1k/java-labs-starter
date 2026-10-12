package edu.course.lab02;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

    @Test
    void shouldCreateAccountWithInitialBalance() {
        BankAccount account = new BankAccount(1000);

        assertEquals(1000, account.getBalance());
    }

    @Test
    void shouldThrowExceptionForNegativeInitialBalance() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BankAccount(-100)
        );
    }

    @Test
    void shouldDepositMoney() {
        BankAccount account = new BankAccount(1000);

        account.deposit(500);

        assertEquals(1500, account.getBalance());
    }

    @Test
    void shouldWithdrawMoney() {
        BankAccount account = new BankAccount(1000);

        account.withdraw(300);

        assertEquals(700, account.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenWithdrawingMoreThanBalance() {
        BankAccount account = new BankAccount(500);

        assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(700)
        );

        assertEquals(500, account.getBalance());
    }
}