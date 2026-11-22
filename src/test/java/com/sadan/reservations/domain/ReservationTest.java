package com.sadan.reservations.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ReservationTest {

    @Test
    void newReservationIsConfirmedAndActive() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        Reservation r = new Reservation("R1", table, customer, slot, 2);
        assertEquals(ReservationStatus.CONFIRMED, r.getStatus());
        assertTrue(r.isActive());
    }

    @Test
    void gettersReturnWhatWasGiven() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        Reservation r = new Reservation("R1", table, customer, slot, 2);
        assertEquals("R1", r.getId());
        assertSame(table, r.getTable());
        assertSame(customer, r.getCustomer());
        assertSame(slot, r.getSlot());
        assertEquals(2, r.getPartySize());
    }


    @Test
    void seatMovesToSeated() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        Reservation r = new Reservation("R1", table, customer, slot, 2);
        r.seat();
        assertEquals(ReservationStatus.SEATED, r.getStatus());
        assertTrue(r.isActive());
    }

    @Test
    void seatThenCompleteMovesToCompleted() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        Reservation r = new Reservation("R1", table, customer, slot, 2);
        r.seat();
        r.complete();
        assertEquals(ReservationStatus.COMPLETED, r.getStatus());
        assertFalse(r.isActive());
    }

    @Test
    void cancelFromConfirmedMovesToCancelled() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        Reservation r = new Reservation("R1", table, customer, slot, 2);
        r.cancel();
        assertEquals(ReservationStatus.CANCELLED, r.getStatus());
        assertFalse(r.isActive());
    }

    @Test
    void completeStraightAwayThrows() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        Reservation r = new Reservation("R1", table, customer, slot, 2);
        assertThrows(IllegalStateException.class, () -> r.complete());
        assertEquals(ReservationStatus.CONFIRMED, r.getStatus());
    }

    @Test
    void seatAfterCancelThrows() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        Reservation r = new Reservation("R1", table, customer, slot, 2);
        r.cancel();
        assertThrows(IllegalStateException.class, () -> r.seat());
        assertEquals(ReservationStatus.CANCELLED, r.getStatus());
    }

    @Test
    void completeAfterCancelThrows() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        Reservation r = new Reservation("R1", table, customer, slot, 2);
        r.cancel();
        assertThrows(IllegalStateException.class, () -> r.complete());
    }

    @Test
    void cancelTwiceThrows() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        Reservation r = new Reservation("R1", table, customer, slot, 2);
        r.cancel();
        assertThrows(IllegalStateException.class, () -> r.cancel());
    }
  
    @Test
    void seatTwiceThrows() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        Reservation r = new Reservation("R1", table, customer, slot, 2);
        r.seat();
        assertThrows(IllegalStateException.class, () -> r.seat());
    }
  
    @Test
    void cancelAfterSeatedThrows() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        Reservation r = new Reservation("R1", table, customer, slot, 2);
        r.seat();
        assertThrows(IllegalStateException.class, () -> r.cancel());
        assertEquals(ReservationStatus.SEATED, r.getStatus());
    }

    @Test
    void anyMoveAfterCompletedThrows() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        Reservation r = new Reservation("R1", table, customer, slot, 2);
        r.seat();
        r.complete();
        assertThrows(IllegalStateException.class, () -> r.seat());
        assertThrows(IllegalStateException.class, () -> r.complete());
        assertThrows(IllegalStateException.class, () -> r.cancel());
        assertEquals(ReservationStatus.COMPLETED, r.getStatus());
    }


    @Test
    void partySizeZeroThrows() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        assertThrows(IllegalArgumentException.class, () -> new Reservation("R1", table, customer, slot, 0));
    }

    @Test
    void negativePartySizeThrows() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        assertThrows(IllegalArgumentException.class, () -> new Reservation("R1", table, customer, slot, -3));
    }

    @Test
    void nullIdThrows() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        assertThrows(IllegalArgumentException.class, () -> new Reservation(null, table, customer, slot, 2));
    }

    @Test
    void nullTableThrows() {
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        assertThrows(IllegalArgumentException.class, () -> new Reservation("R1", null, customer, slot, 2));
    }

    @Test
    void nullCustomerThrows() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 11, 20, 19, 0), LocalDateTime.of(2026, 11, 20, 21, 0));
        assertThrows(IllegalArgumentException.class, () -> new Reservation("R1", table, null, slot, 2));
    }
    @Test
    void nullSlotThrows() {
        DiningTable table = new StandardTable("T1", 4, Zone.WINDOW, new Position(1, 1));
        Customer customer = new Customer("Sara", "0501234567", "sara@gmail.com");
        assertThrows(IllegalArgumentException.class, () -> new Reservation("R1", table, customer, null, 2));
    }
}
