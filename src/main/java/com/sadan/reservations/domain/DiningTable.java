package com.sadan.reservations.domain;

public abstract class DiningTable {
    private final String label;
    private final int seats;
    private final Zone zone;
    private final Position position;
    // protected so only subclasses can call it!
    protected DiningTable(String label, int seats, Zone zone, Position position) {

        if (seats<1 ) {throw new IllegalArgumentException("seats must be a valid value over 1. ");}
        if (label==null || label.isBlank()){throw new IllegalArgumentException("label must be entered.");}

        this.label = label;
        this.seats = seats;
        this.zone = zone;
        this.position = position;

    }

    public String getLabel() {
        return label;
    }

    public int getSeats() {
        return seats;
    }

    public Zone getZone() {
        return zone;
    }

    public Position getPosition() {
        return position;
    }

    public abstract boolean canSeat(int partySize);
}