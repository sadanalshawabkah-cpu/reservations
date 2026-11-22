package com.sadan.reservations.repository;
import com.sadan.reservations.domain.Reservation;

import java.time.LocalDate;
import java.util.*;

public class InMemoryReservationRepository implements ReservationRepository {

    private final Map<String, Reservation> store = new HashMap<>();

    @Override
    public void save(Reservation reservation) {
        store.put(reservation.getId(), reservation);

    }

    @Override
    public Optional<Reservation> findById(String id) {
        return Optional.ofNullable(store.get(id));

    }

    @Override
    public List<Reservation> findByDate(LocalDate date) {
        List<Reservation> matches = new ArrayList<>();

        for (Reservation r : store.values()) {
            LocalDate day = r.getSlot().start().toLocalDate();
            if (day.equals(date)) {
                matches.add(r);
            }
        }
        return matches;
    }
    @Override
    public void delete(String id){
        store.remove(id);

    }


}
