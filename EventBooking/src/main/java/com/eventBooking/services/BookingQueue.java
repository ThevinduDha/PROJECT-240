package com.eventBooking.services;

import com.eventBooking.models.booking.Booking;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

public class BookingQueue implements Iterable<Booking> {
    
    private class Node {
        Booking booking;
        Node next;
        
        Node(Booking booking) {
            this.booking = booking;
            this.next = null;
        }
    }
    
    private Node head; 
    private Node tail; 
    private int size; 
    
    public BookingQueue() {
        head = null;
        tail = null;
        size = 0;
    }

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
    

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
    

    public List<Booking> toArrayList() {
        List<Booking> list = new ArrayList<>(size);
        Node current = head;
        
        while (current != null) {
            list.add(current.booking);
            current = current.next;
        }
        
        return list;
    }
    

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
