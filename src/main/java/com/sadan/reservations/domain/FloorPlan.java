package com.sadan.reservations.domain;

import java.util.List;

public class FloorPlan {

    public static List<DiningTable> tables() {
        return List.of(
            new StandardTable("T1", 2, Zone.WINDOW,   new Position(2, 1)),
            new BoothTable   ("B1", 6, Zone.INDOOR,   new Position(2, 6)),
            new BarSeat      ("S1", 1, Zone.WINDOW,   new Position(0, 0)),
            new StandardTable("T2", 6, Zone.TERRANCE, new Position(5, 1)),
            new BoothTable   ("B2", 4, Zone.INDOOR,   new Position(5, 6)),
            new BarSeat      ("S2", 1, Zone.WINDOW,   new Position(0, 1)),
            new StandardTable("T3", 2, Zone.TERRANCE, new Position(8, 1)),
            new BoothTable   ("B3", 6, Zone.INDOOR,   new Position(8, 6)),
            new BarSeat      ("S3", 1, Zone.WINDOW,   new Position(0, 2)),
            new StandardTable("T4", 6, Zone.INDOOR,   new Position(2, 3)),
            new StandardTable("T5", 2, Zone.INDOOR,   new Position(5, 3)),
            new StandardTable("T6", 6, Zone.TERRANCE, new Position(8, 3)),
            new BarSeat      ("S4", 1, Zone.WINDOW,   new Position(0, 3)),
            new BarSeat      ("S5", 1, Zone.WINDOW,   new Position(0, 4))
        );
    }
}
