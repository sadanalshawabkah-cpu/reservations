package com.sadan.reservations.service;

import com.sadan.reservations.domain.Customer;
import com.sadan.reservations.domain.FloorPlan;
import com.sadan.reservations.domain.Reservation;
import com.sadan.reservations.domain.TimeSlot;
import com.sadan.reservations.exception.InvalidBookingException;
import com.sadan.reservations.exception.TableUnavailableException;
import com.sadan.reservations.repository.InMemoryReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {

    private ReservationService service;
    private final Customer sara = new Customer("Sara", "0501234567", "sara@gmail.com");
    private final Customer ali = new Customer("Ali", "0507654321", "ali@gmail.com");

    private TimeSlot slot(int startHour, int endHour) {
        return new TimeSlot(LocalDateTime.of(2026, 11, 20, startHour, 0),
                            LocalDateTime.of(2026, 11, 20, endHour, 0));
    }

    @BeforeEach
    void setUp() {
        service = new ReservationService(FloorPlan.tables(), new InMemoryReservationRepository());
    }

    @Test
    void bookingAFreeTableSavesIt() {
        Reservation r = service.book("T1", sara, slot(19, 21), 2);
        assertEquals("R-1", r.getId());
        assertEquals(1, service.bookingsOn(r.getSlot().start().toLocalDate()).size());
    }

    @Test
    void overlappingBookingIsRefused() {
        service.book("T1", sara, slot(19, 21), 2);
        assertThrows(TableUnavailableException.class, () -> service.book("T1", ali, slot(20, 22), 2));
    }

    @Test
    void touchingBookingIsAllowed() {
        service.book("T1", sara, slot(19, 21), 2);
        assertDoesNotThrow(() -> service.book("T1", ali, slot(21, 23), 2));
    }

    @Test
    void partyTooBigIsRefused() {
        assertThrows(InvalidBookingException.class, () -> service.book("T1", ali, slot(19, 21), 5));
    }

    @Test
    void cancelledBookingFreesTheTable() {
        Reservation r = service.book("T1", sara, slot(19, 21), 2);
        service.cancel(r.getId());
        assertDoesNotThrow(() -> service.book("T1", ali, slot(19, 21), 2));
    }

    @Test
    void unknownTableIsRefused() {
        assertThrows(InvalidBookingException.class, () -> service.book("Z9", sara, slot(19, 21), 2));
    }
}
