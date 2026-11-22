package com.sadan.reservations.exception;

public class InvalidBookingException extends ReservationException {
    public InvalidBookingException(String msg) {
        super(msg);
    }
}
