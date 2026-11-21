package com.sadan.reservations.ui;

import com.sadan.reservations.domain.*;
import com.sadan.reservations.exception.ReservationException;
import com.sadan.reservations.service.ReservationService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class MainWindow extends JFrame {

    private static final int COLUMNS = 10;
    private static final int ROWS = 8;
    private static final Color FREE = new Color(76, 175, 80);
    private static final Color TAKEN = new Color(229, 57, 53);
    private static final Color TOO_SMALL = new Color(189, 189, 189);

    private final ReservationService service;

    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 10);
    private final JComboBox<String> timeBox =
            new JComboBox<>(new String[]{"17:00", "18:00", "19:00", "20:00", "21:00", "22:00"});
    private final JSpinner partySpinner = new JSpinner(new SpinnerNumberModel(2, 1, 6, 1));
    private final JPanel floor = new JPanel(new GridLayout(ROWS, COLUMNS, 6, 6));

    public MainWindow(ReservationService service) {
        super("Restaurant Reservations");
        this.service = service;

        JButton showButton = new JButton("Show tables");
        showButton.addActionListener(e -> refresh());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Date (yyyy-mm-dd):"));
        controls.add(dateField);
        controls.add(new JLabel("Time:"));
        controls.add(timeBox);
        controls.add(new JLabel("Guests:"));
        controls.add(partySpinner);
        controls.add(showButton);

        JButton cancelButton = new JButton("Cancel a booking");
        cancelButton.addActionListener(e -> cancelBooking());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(legend(FREE, "Free"));
        bottom.add(legend(TAKEN, "Booked"));
        bottom.add(legend(TOO_SMALL, "Wrong size"));
        bottom.add(cancelButton);

        floor.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        setLayout(new BorderLayout());
        add(controls, BorderLayout.NORTH);
        add(floor, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 720);
        setLocationRelativeTo(null);
        refresh();
    }

    /** Redraws the floor: one cell per grid square, a coloured button where a table stands. */
    private void refresh() {
        TimeSlot slot = chosenSlot();
        if (slot == null) {
            return;
        }
        int partySize = (Integer) partySpinner.getValue();

        floor.removeAll();
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                DiningTable table = tableAt(x, y);
                if (table == null) {
                    floor.add(new JLabel());
                } else {
                    floor.add(tableButton(table, slot, partySize));
                }
            }
        }
        floor.revalidate();
        floor.repaint();
    }

    private JButton tableButton(DiningTable table, TimeSlot slot, int partySize) {
        JButton button = new JButton("<html><center><b>" + table.getLabel() + "</b><br>"
                + table.getSeats() + " seats</center></html>");
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setToolTipText(table.getClass().getSimpleName() + " · " + table.getZone());

        if (!table.canSeat(partySize)) {
            button.setBackground(TOO_SMALL);
            button.setEnabled(false);
        } else if (!service.isFree(table, slot)) {
            button.setBackground(TAKEN);
            button.setEnabled(false);
        } else {
            button.setBackground(FREE);
            button.addActionListener(e -> book(table, slot, partySize));
        }
        return button;
    }

    private void book(DiningTable table, TimeSlot slot, int partySize) {
        JTextField name = new JTextField();
        JTextField phone = new JTextField();
        JTextField email = new JTextField();
        Object[] form = {"Name:", name, "Phone:", phone, "Email:", email};

        int choice = JOptionPane.showConfirmDialog(this, form,
                "Book " + table.getLabel() + " for " + partySize, JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            Customer customer = new Customer(name.getText(), phone.getText(), email.getText());
            Reservation r = service.book(table.getLabel(), customer, slot, partySize);
            JOptionPane.showMessageDialog(this, "Booked! Your booking id is " + r.getId());
            refresh();
        } catch (IllegalArgumentException | ReservationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Could not book", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelBooking() {
        String id = JOptionPane.showInputDialog(this, "Booking id to cancel (e.g. R-1):");
        if (id == null || id.isBlank()) {
            return;
        }
        try {
            service.cancel(id.trim());
            JOptionPane.showMessageDialog(this, "Booking " + id.trim() + " cancelled.");
            refresh();
        } catch (IllegalStateException | ReservationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Could not cancel", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Reads the date and time boxes and turns them into a 2-hour slot; null if the date is invalid. */
    private TimeSlot chosenSlot() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            LocalTime time = LocalTime.parse((String) timeBox.getSelectedItem());
            LocalDateTime start = LocalDateTime.of(date, time);
            return new TimeSlot(start, start.plusHours(2));
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Please type the date as yyyy-mm-dd, e.g. 2026-11-21");
            return null;
        }
    }

    private DiningTable tableAt(int x, int y) {
        for (DiningTable t : service.getTables()) {
            if (t.getPosition().x() == x && t.getPosition().y() == y) {
                return t;
            }
        }
        return null;
    }

    private static JLabel legend(Color color, String text) {
        JLabel label = new JLabel("  " + text + "  ");
        label.setOpaque(true);
        label.setBackground(color);
        label.setForeground(Color.WHITE);
        return label;
    }
}
