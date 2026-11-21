package com.sadan.reservations.domain;


    public class Reservation{
    private final String id;
    private final DiningTable table;
    private final Customer customer; 
    private final TimeSlot slot;
    private final int partySize;
    private ReservationStatus status;
    
    public Reservation(String id, DiningTable table, Customer customer, TimeSlot slot, int partySize){
        if (id == null || table == null || customer == null || slot == null) {
            throw new IllegalArgumentException("id, table, customer and slot are required");
        }
        if (partySize < 1) {
            throw new IllegalArgumentException("party size must be at least 1");
        }
        this.id=id;
        this.table=table;
        this.customer=customer;
        this.slot=slot;
        this.partySize=partySize;
        this.status=ReservationStatus.CONFIRMED;
    }
        
    public void seat() {
        if (status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("cannot seat a reservation that is "+status);
        }
        status = ReservationStatus.SEATED;
    }
    
    public void complete() {
        if (status != ReservationStatus.SEATED) {
            throw new IllegalStateException("cannot complete a reservation that is "+status);
        }
        status = ReservationStatus.COMPLETED;
    }
    
    public void cancel() {
        if (status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("cannot cancel a reservation that is "+status);
        }
        status = ReservationStatus.CANCELLED;
    }
    
    public boolean isActive() {
        return status == ReservationStatus.CONFIRMED || status == ReservationStatus.SEATED;
    }
    
    public String getId() {return id;}
    public DiningTable getTable() {return table;}
    public Customer getCustomer() {return customer;}
    public TimeSlot getSlot() {return slot;}
    public int getPartySize() {return partySize;}
    public ReservationStatus getStatus() {return status;}
}    




