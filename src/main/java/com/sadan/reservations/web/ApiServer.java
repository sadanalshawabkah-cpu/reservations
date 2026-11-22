package com.sadan.reservations.web;

import com.sadan.reservations.domain.*;
import com.sadan.reservations.exception.ReservationException;
import com.sadan.reservations.service.ReservationService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApiServer {

    private final ReservationService service;

    public ApiServer(ReservationService service) {
        this.service = service;
    }

    public void start(int port) {
        Javalin app = Javalin.create(config -> config.staticFiles.add("/public", Location.CLASSPATH));

        app.get("/api/availability", this::availability);
        app.post("/api/reservations", this::book);
        app.post("/api/reservations/{id}/cancel", this::cancel);

        app.exception(ReservationException.class, (e, ctx) -> ctx.status(409).json(Map.of("error", e.getMessage())));
        app.exception(IllegalArgumentException.class, (e, ctx) -> ctx.status(400).json(Map.of("error", e.getMessage())));
        app.exception(IllegalStateException.class, (e, ctx) -> ctx.status(409).json(Map.of("error", e.getMessage())));

        app.start(port);
    }

    private void availability(Context ctx) {
        TimeSlot slot = slotFrom(ctx.queryParam("from"), ctx.queryParam("to"));
        int party = Integer.parseInt(ctx.queryParam("party"));

        List<TableView> result = new ArrayList<>();
        for (DiningTable t : service.getTables()) {
            String status;
            if (!t.canSeat(party)) {
                status = "TOO_SMALL";
            } else if (!service.isFree(t, slot)) {
                status = "TAKEN";
            } else {
                status = "FREE";
            }
            result.add(new TableView(t.getLabel(), t.getSeats(), t.getZone().name(),
                    t.getPosition().x(), t.getPosition().y(), t.getClass().getSimpleName(), status));
        }
        ctx.json(result);
    }

    private void book(Context ctx) {
        TimeSlot slot = slotFrom(ctx.formParam("from"), ctx.formParam("to"));
        Customer customer = new Customer(ctx.formParam("name"), ctx.formParam("phone"), ctx.formParam("email"));
        int party = Integer.parseInt(ctx.formParam("party"));

        Reservation r = service.book(ctx.formParam("table"), customer, slot, party);
        ctx.json(Map.of("id", r.getId()));
    }

    private void cancel(Context ctx) {
        String id = ctx.pathParam("id");
        service.cancel(id);
        ctx.json(Map.of("id", id));
    }

    private TimeSlot slotFrom(String from, String to) {
        return new TimeSlot(LocalDateTime.parse(from), LocalDateTime.parse(to));
    }
}