package com.sadan.reservations.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionsTest {

    @Test
    void tableUnavailableKeepsItsMessage() {
        TableUnavailableException e = new TableUnavailableException("T1 is taken");
        assertEquals("T1 is taken", e.getMessage());
    }

    @Test
    void invalidBookingKeepsItsMessage() {
        InvalidBookingException e = new InvalidBookingException("Bad booking");
        assertEquals("Bad booking", e.getMessage());
    }

    @Test
    void bothAreReservationExceptions() {
        assertInstanceOf(ReservationException.class, new TableUnavailableException("x"));
        assertInstanceOf(ReservationException.class, new InvalidBookingException("x"));
    }
}
