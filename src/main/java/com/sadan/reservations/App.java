package com.sadan.reservations;

import com.sadan.reservations.domain.FloorPlan;
import com.sadan.reservations.repository.InMemoryReservationRepository;
import com.sadan.reservations.service.ReservationService;
import com.sadan.reservations.web.ApiServer;

public class App {
    public static void main(String[] args) {
        ReservationService service =
                new ReservationService(FloorPlan.tables(), new InMemoryReservationRepository());
        new ApiServer(service).start(7070);
    }
}