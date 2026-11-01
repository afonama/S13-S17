package com.aptech.library.domain;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    List<Book> findAll();

    Optional<Book> findById(int id);

    Optional<Book> findByIsbn(String isbn);

    Book create(Book book);

    void update(Book book);

    void delete(int id);
}