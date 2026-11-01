package com.aptech.library.domain;

import java.util.Objects;

public final class Book {

    private final int id;
    private String title;
    private String author;
    private String isbn;
    private boolean available;

    public Book(int id, String title, String author, String isbn, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.available = available;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void toggleAvailability() {
        this.available = !this.available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Book other)) {
            return false;
        }

        if (id > 0 && other.id > 0) {
            return id == other.id;
        }

        return available == other.available
                && Objects.equals(title, other.title)
                && Objects.equals(author, other.author)
                && Objects.equals(isbn, other.isbn);
    }

    @Override
    public int hashCode() {
        if (id > 0) {
            return Objects.hash(id);
        }

        return Objects.hash(title, author, isbn, available);
    }

    @Override
    public String toString() {
        return title == null ? "Untitled" : title;
    }
}