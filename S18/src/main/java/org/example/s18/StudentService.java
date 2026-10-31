package org.example.s18;

import java.util.ArrayList;
import java.util.List;

public class StudentService {

    private static final int CAPACITY = 30;
    private final List<String> students = new ArrayList<>();

    /**
     * 4-stage validation pipeline:
     *  1. null/blank check
     *  2. trim() whitespace (Java 8 compatible replacement for strip())
     *  3. duplicate check (case-insensitive)
     *  4. capacity check (>= 30)
     */
    public void add(String name) {
        // Stage 1: null or blank (Java 8 compatible)
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }

        // Stage 2: trim whitespace
        String clean = name.trim();

        // Stage 3: duplicate check (case-insensitive)
        boolean duplicate = students.stream()
                .anyMatch(existing -> existing.equalsIgnoreCase(clean));
        if (duplicate) {
            throw new IllegalArgumentException("Student already registered: " + clean);
        }

        // Stage 4: capacity check
        if (students.size() >= CAPACITY) {
            throw new IllegalArgumentException("Class is full (capacity " + CAPACITY + ")");
        }

        students.add(clean);
    }

    public List<String> findAll() {
        return new ArrayList<>(students);
    }

    public int size() {
        return students.size();
    }

    public int capacity() {
        return CAPACITY;
    }

    public int availableSeats() {
        return CAPACITY - students.size();
    }

    public void reset() {
        students.clear();
    }
}