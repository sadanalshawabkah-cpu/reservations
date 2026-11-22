package com.sadan.reservations.exception;

public class TableUnavailableException extends ReservationException {
    public TableUnavailableException(String msg) {
        super(msg);
    }
}
