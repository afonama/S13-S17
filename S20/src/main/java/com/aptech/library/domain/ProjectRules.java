package com.aptech.library.domain;

import java.util.ArrayList;
import java.util.List;

public final class ProjectRules {

    public static final int TITLE_MIN_LENGTH = 2;
    public static final int TITLE_MAX_LENGTH = 150;
    public static final int AUTHOR_MIN_LENGTH = 2;
    public static final int AUTHOR_MAX_LENGTH = 100;

    private ProjectRules() {
        // Utility class
    }

    public static void validate(Book book) {
        if (book == null) {
            throw new ValidationException("Book cannot be null.");
        }

        List<String> errors = new ArrayList<>();

        validateTitle(book.getTitle(), errors);
        validateAuthor(book.getAuthor(), errors);
        validateIsbn(book.getIsbn(), errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(System.lineSeparator(), errors));
        }
    }

    public static String normalizeIsbn(String isbn) {
        if (isbn == null) {
            return null;
        }

        String cleaned = isbn.replaceAll("[\\s-]", "").toUpperCase();

        if (cleaned.isEmpty()) {
            return null;
        }

        return cleaned;
    }

    public static boolean isValidIsbn(String isbn) {
        String normalized = normalizeIsbn(isbn);

        if (normalized == null) {
            return false;
        }

        return switch (normalized.length()) {
            case 10 -> isValidIsbn10(normalized);
            case 13 -> isValidIsbn13(normalized);
            default -> false;
        };
    }

    private static void validateTitle(String title, List<String> errors) {
        String trimmed = title == null ? "" : title.trim();

        if (trimmed.length() < TITLE_MIN_LENGTH || trimmed.length() > TITLE_MAX_LENGTH) {
            errors.add("Title must be between " + TITLE_MIN_LENGTH
                    + " and " + TITLE_MAX_LENGTH + " characters.");
        }
    }

    private static void validateAuthor(String author, List<String> errors) {
        String trimmed = author == null ? "" : author.trim();

        if (trimmed.length() < AUTHOR_MIN_LENGTH || trimmed.length() > AUTHOR_MAX_LENGTH) {
            errors.add("Author must be between " + AUTHOR_MIN_LENGTH
                    + " and " + AUTHOR_MAX_LENGTH + " characters.");
        }
    }

    private static void validateIsbn(String isbn, List<String> errors) {
        String normalized = normalizeIsbn(isbn);

        if (normalized == null) {
            errors.add("ISBN is required.");
            return;
        }

        if (!isValidIsbn(normalized)) {
            errors.add("ISBN must be a valid ISBN-10 or ISBN-13.");
        }
    }

    private static boolean isValidIsbn10(String isbn) {
        int sum = 0;

        for (int i = 0; i < 10; i++) {
            char c = isbn.charAt(i);
            int digit;

            if (i == 9 && c == 'X') {
                digit = 10;
            } else if (Character.isDigit(c)) {
                digit = c - '0';
            } else {
                return false;
            }

            sum += digit * (10 - i);
        }

        return sum % 11 == 0;
    }

    private static boolean isValidIsbn13(String isbn) {
        int sum = 0;

        for (int i = 0; i < 13; i++) {
            char c = isbn.charAt(i);

            if (!Character.isDigit(c)) {
                return false;
            }

            int digit = c - '0';

            if (i % 2 == 0) {
                sum += digit;
            } else {
                sum += digit * 3;
            }
        }

        return sum % 10 == 0;
    }
}