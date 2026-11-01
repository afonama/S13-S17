package com.aptech.library.data;

import com.aptech.library.domain.Book;
import com.aptech.library.domain.BookRepository;
import com.aptech.library.domain.RepositoryException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcBookRepository implements BookRepository {

    private final String jdbcUrl;

    public JdbcBookRepository(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        initializeSchema();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    private void initializeSchema() {
        String sql = """
                CREATE TABLE IF NOT EXISTS books (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    author TEXT NOT NULL,
                    isbn TEXT NOT NULL UNIQUE,
                    available INTEGER NOT NULL DEFAULT 1
                )
                """;

        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {

            statement.execute(sql);

        } catch (SQLException ex) {
            throw new RepositoryException(
                    "Could not initialize database schema. " + ex.getMessage(),
                    ex
            );
        }
    }

    @Override
    public List<Book> findAll() {
        String sql = """
                SELECT id, title, author, isbn, available
                FROM books
                ORDER BY id
                """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            List<Book> books = new ArrayList<>();

            while (resultSet.next()) {
                books.add(mapRow(resultSet));
            }

            return books;

        } catch (SQLException ex) {
            throw translate(ex, "Could not load books.");
        }
    }

    @Override
    public Optional<Book> findById(int id) {
        String sql = """
                SELECT id, title, author, isbn, available
                FROM books
                WHERE id = ?
                """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }

                return Optional.empty();
            }

        } catch (SQLException ex) {
            throw translate(ex, "Could not find book by id.");
        }
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        String sql = """
                SELECT id, title, author, isbn, available
                FROM books
                WHERE isbn = ?
                """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, isbn);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }

                return Optional.empty();
            }

        } catch (SQLException ex) {
            throw translate(ex, "Could not find book by ISBN.");
        }
    }

    @Override
    public Book create(Book book) {
        String sql = """
                INSERT INTO books (title, author, isbn, available)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getIsbn());
            statement.setInt(4, book.isAvailable() ? 1 : 0);

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Book(
                            generatedKeys.getInt(1),
                            book.getTitle(),
                            book.getAuthor(),
                            book.getIsbn(),
                            book.isAvailable()
                    );
                }
            }

            throw new RepositoryException("Book insert completed but no ID was generated.");

        } catch (SQLException ex) {
            throw translate(ex, "Could not add book.");
        }
    }

    @Override
    public void update(Book book) {
        String sql = """
                UPDATE books
                SET title = ?, author = ?, isbn = ?, available = ?
                WHERE id = ?
                """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getIsbn());
            statement.setInt(4, book.isAvailable() ? 1 : 0);
            statement.setInt(5, book.getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new RepositoryException("Book id " + book.getId() + " was not found.");
            }

        } catch (SQLException ex) {
            throw translate(ex, "Could not update book.");
        }
    }

    @Override
    public void delete(int id) {
        String sql = """
                DELETE FROM books
                WHERE id = ?
                """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new RepositoryException("Book id " + id + " was not found.");
            }

        } catch (SQLException ex) {
            throw translate(ex, "Could not delete book.");
        }
    }

    private Book mapRow(ResultSet resultSet) throws SQLException {
        return new Book(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("author"),
                resultSet.getString("isbn"),
                resultSet.getInt("available") == 1
        );
    }

    private RepositoryException translate(SQLException ex, String userMessage) {
        String sqlMessage = ex.getMessage() == null ? "" : ex.getMessage();

        if (sqlMessage.contains("UNIQUE") || sqlMessage.contains("constraint")) {
            return new RepositoryException(
                    "A book with the same ISBN already exists.",
                    ex
            );
        }

        return new RepositoryException(userMessage + " Details: " + sqlMessage, ex);
    }
}