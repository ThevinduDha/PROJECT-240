package com.eventBooking.services;

import com.eventBooking.models.booking.Booking;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Custom Queue implementation for Booking objects.
 * This implementation is based on a linked list structure.
 */
public class BookingQueue implements Iterable<Booking> {
    
    // Node class for the linked list
    private class Node {
        Booking booking;
        Node next;
        
        Node(Booking booking) {
            this.booking = booking;
            this.next = null;
        }
    }
    
    private Node head; // Front of the queue
    private Node tail; // Rear of the queue
    private int size;  // Number of elements in the queue
    
    public BookingQueue() {
        head = null;
        tail = null;
        size = 0;
    }
    
    /**
     * Add a booking to the end of the queue
     * @param booking The booking to add
     * @return true if the booking was added successfully
     */
    public boolean add(Booking booking) {
        Node newNode = new Node(booking);
        
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        
        size++;
        return true;
    }
    
    /**
     * Check if the queue is empty
     * @return true if the queue is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Get the number of elements in the queue
     * @return The number of elements
     */
    public int size() {
        return size;
    }
    
    /**
     * Convert the queue to an ArrayList
     * @return An ArrayList containing all bookings in the queue
     */
    public List<Booking> toArrayList() {
        List<Booking> list = new ArrayList<>(size);
        Node current = head;
        
        while (current != null) {
            list.add(current.booking);
            current = current.next;
        }
        
        return list;
    }
    
    /**
     * Find the first booking that matches the given predicate
     * @param predicate The predicate to match
     * @return An Optional containing the first matching booking, or empty if none found
     */
    public Optional<Booking> findFirst(Predicate<Booking> predicate) {
        Node current = head;
        
        while (current != null) {
            if (predicate.test(current.booking)) {
                return Optional.of(current.booking);
            }
            current = current.next;
        }
        
        return Optional.empty();
    }
    
    /**
     * Custom iterator implementation for the queue
     */
    @Override
    public Iterator<Booking> iterator() {
        return new Iterator<Booking>() {
            private Node current = head;
            private Node previous = null;
            private Node beforePrevious = null;
            
            @Override
            public boolean hasNext() {
                return current != null;
            }
            
            @Override
            public Booking next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                
                Booking booking = current.booking;
                beforePrevious = previous;
                previous = current;
                current = current.next;
                return booking;
            }
            
            @Override
            public void remove() {
                if (previous == null) {
                    throw new IllegalStateException("next() has not been called");
                }
                
                if (beforePrevious == null) {
                    // Removing the first element
                    head = current;
                } else {
                    // Removing an element in the middle
                    beforePrevious.next = current;
                }
                
                // If we're removing the last element, update the tail
                if (previous == tail) {
                    tail = beforePrevious;
                }
                
                previous = beforePrevious;
                size--;
            }
        };
    }
}