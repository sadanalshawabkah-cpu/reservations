package com.sadan.reservations.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class BarSeatTest {

    @Test
    void AcceptsPartyOf2() {
        BarSeat BS = new BarSeat("S1", 2, Zone.INDOOR, new Position(0, 0));
        assertTrue(BS.canSeat(2));

    }
    @Test
    void threeSeatsThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new BarSeat("S2", 3, Zone.INDOOR, new Position(0, 1))
        );

    }
}
