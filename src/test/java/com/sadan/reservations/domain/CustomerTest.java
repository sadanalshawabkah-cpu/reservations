package com.sadan.reservations.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void validCustomerKeepsItsDetails() {
        Customer c = new Customer("Sara", "0501234567", "sara@gmail.com");
        assertEquals("Sara", c.getName());
        assertEquals("0501234567", c.getPhone());
        assertEquals("sara@gmail.com", c.getEmail());
    }

    @Test
    void blankNameIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer("   ", "0501234567", "sara@gmail.com"));
    }

    @Test
    void missingPhoneIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer("Sara", null, "sara@gmail.com"));
    }

    @Test
    void emailWithoutAtSignIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer("Sara", "0501234567", "sara.gmail.com"));
    }
}
