package com.sadan.reservations.service;
import com.sadan.reservations.domain.*;
import com.sadan.reservations.exception.InvalidBookingException;
import com.sadan.reservations.exception.TableUnavailableException;
import com.sadan.reservations.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;

public class ReservationService {

    private final List<DiningTable> tables;
    private final ReservationRepository repository;
    private int nextId=1;

    public ReservationService(List<DiningTable> tables, ReservationRepository repository){
        this.tables=tables;
        this.repository=repository;
    }

    public List<DiningTable> getTables(){
        return tables;
    }

    public List<Reservation> bookingsOn(LocalDate date){
     return repository.findByDate(date);
    }
    public boolean isFree(DiningTable table, TimeSlot slot){
        LocalDate day=slot.start().toLocalDate();
        for (Reservation r: repository.findByDate(day)) {
            if (r.getTable() == table && r.isActive() && r.getSlot().overlaps(slot)) {
                return false;
            }
        } return true;
    }
    public Reservation book(String tableLabel, Customer customer, TimeSlot slot,int partySize){
        DiningTable table = findTable(tableLabel);

        if(!table.canSeat(partySize)){throw new InvalidBookingException(tableLabel + " cannot seat " + partySize +" guests.");
        }
        if(!isFree(table,slot)){throw new TableUnavailableException(tableLabel + " is already booked at that time. ");
        }

        Reservation reservation = new Reservation("R-" + nextId++,table,customer,slot,partySize);
        repository.save(reservation);
        return reservation;

    }
    public void cancel(String id){
        Reservation reservation=repository.findById(id)
                .orElseThrow(()-> new InvalidBookingException("No Booking with id "+ id));
        reservation.cancel();
        repository.save(reservation);
    }
    private DiningTable findTable(String label){
        for(DiningTable t: tables){
            if(t.getLabel().equals(label)){
                return t;
            }
        }
        throw new InvalidBookingException("no table called "+ label);
    }
}

