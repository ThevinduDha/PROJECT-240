package com.eventBooking.services;

import com.eventBooking.models.booking.Booking;

import java.io.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private static final String BOOKING_FILE = "data/bookings.txt";
    private final BookingQueue bookingQueue = new BookingQueue();

    public BookingService() {
        loadBookingsToQueue();
    }

    public Booking getBookingById(String bookingId, String username) {
        logger.debug("Searching for booking with ID: {} for user: {}", bookingId, username);

        // First, ensure the bookings are loaded
        if (bookingQueue.isEmpty()) {
            loadBookingsToQueue();
        }

        // Get all bookings for the user
        List<Booking> userBookings = getBookingsByUser(username);
        logger.debug("Found {} bookings for user {}", userBookings.size(), username);

        // Search for the specific booking
        for (Booking booking : userBookings) {
            if (booking.getBookingId().equals(bookingId)) {
                logger.debug("Found matching booking: {}", booking);
                return booking;
            }
        }

        logger.warn("No booking found with ID: {} for user: {}", bookingId, username);
        return null;
    }

    private void loadBookingsToQueue() {
        logger.debug("Loading bookings from file: {}", BOOKING_FILE);
        File bookingFile = new File(BOOKING_FILE);

        // Create the directory if it doesn't exist
        if (!bookingFile.getParentFile().exists()) {
            bookingFile.getParentFile().mkdirs();
        }

        // Create the file if it doesn't exist
        if (!bookingFile.exists()) {
            try {
                bookingFile.createNewFile();
                logger.info("Created new bookings file");
            } catch (IOException e) {
                logger.error("Error creating bookings file: {}", e.getMessage());
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKING_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Booking booking = Booking.fromFileString(line);
                    bookingQueue.add(booking);
                    logger.debug("Loaded booking: {}", booking);
                } catch (Exception e) {
                    logger.error("Error parsing booking line: {}", line, e);
                }
            }
        } catch (IOException e) {
            logger.error("Error loading bookings into queue: {}", e.getMessage());
        }

        logger.info("Loaded {} bookings from file", bookingQueue.size());
    }

    private void saveQueueToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKING_FILE))) {
            for (Booking b : bookingQueue) {
                writer.write(b.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            logger.error("Error saving queue to bookings.txt: {}", e.getMessage());
        }
    }

    public List<Booking> getAllBookings() {
        return bookingQueue.toArrayList();
    }

    public List<Booking> getBookingsByUser(String username) {
        List<Booking> userBookings = new ArrayList<>();
        for (Booking b : bookingQueue) {
            if (b.getUsername().equals(username)) {
                userBookings.add(b);
            }
        }
        return userBookings;
    }

    public boolean createBooking(Booking booking) {
        bookingQueue.add(booking); // Enqueue the new booking
        saveQueueToFile();
        return true;
    }

    public void updateBookingStatus(String username, String providerName, String newStatus) {
        for (Booking b : bookingQueue) {
            if (b.getUsername().equals(username) && b.getProviderName().equals(providerName)) {
                b.setStatus(newStatus);
            }
        }
        saveQueueToFile();
    }

    public void completeBooking(String bookingId) {
        // If you load bookings on demand, you might need to load them first:
        // loadBookingsFromFile(); // Or however you refresh your bookingQueue if it's not always live

        Booking bookingToComplete = null;
        for (Booking b : bookingQueue) { // Assuming bookingQueue is your List<Booking>
            if (b.getBookingId().equals(bookingId)) {
                bookingToComplete = b;
                break;
            }
        }

        if (bookingToComplete != null) {
            bookingToComplete.setStatus("completed"); // Use the setStatus method from Booking.java
            saveQueueToFile(); // Persist the changes
            // logger.info("Booking {} marked as completed.", bookingId);
        } else {
            // logger.warn("Attempted to complete non-existent booking with ID: {}", bookingId);
            throw new RuntimeException("Booking not found with ID: " + bookingId + ". Cannot mark as completed.");
        }
    }

    public void deleteBooking(String username, String bookingId) {
        Iterator<Booking> iterator = bookingQueue.iterator();
        boolean removed = false;

        while (iterator.hasNext()) {
            Booking b = iterator.next();
            if (b.getUsername().equals(username) && b.getBookingId().equals(bookingId)) {
                iterator.remove(); // Dequeue the matched booking
                removed = true;
                break;
            }
        }

        if (removed) {
            saveQueueToFile();
        }
    }

    public Optional<Booking> findBookingById(String bookingId) {
        loadQueueFromFile(); // Load the queue from file to ensure we have the latest data
        return bookingQueue.findFirst(booking -> booking.getBookingId().equals(bookingId));
    }

    /**
     * Loads the booking queue from file.
     * This method is a wrapper for loadBookingsToQueue() for better readability.
     */
    public void loadQueueFromFile() {
        loadBookingsToQueue();
    }



}
