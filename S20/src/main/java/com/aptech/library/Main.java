package com.aptech.library;

import com.aptech.library.data.JdbcBookRepository;
import com.aptech.library.domain.BookRepository;
import com.aptech.library.ui.ProjectApp;

import javafx.application.Application;

public class Main {

    public static void main(String[] args) {
        BookRepository repository = new JdbcBookRepository("jdbc:sqlite:aptech_library.db");

        ProjectApp.injectRepository(repository);

        Application.launch(ProjectApp.class, args);
    }
}