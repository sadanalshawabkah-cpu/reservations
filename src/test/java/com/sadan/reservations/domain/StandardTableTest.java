package com.sadan.reservations.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StandardTableTest {

    @Test
    void seatsPartyEqualToSeats() {
        StandardTable t = new StandardTable("T1", 4, Zone.INDOOR, new Position(1, 1));
        assertTrue(t.canSeat(4));
    }

    @Test
    void rejectsPartyGreaterThanSeats(){
        StandardTable t = new StandardTable("T1", 4, Zone.INDOOR, new Position(1, 1));
        assertFalse(t.canSeat(5));
    }
    @Test
    void rejectsPartyOf0(){
        StandardTable t = new StandardTable("T1", 4, Zone.INDOOR, new Position(1, 1));
        assertFalse(t.canSeat(0));
    }
}
