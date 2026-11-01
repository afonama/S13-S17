package org.example.s19;

import java.sql.SQLException;
import java.util.List;

public class SelfCheck {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== StudentRepository SelfCheck ===\n");

        try {
            StudentRepository repo = new StudentRepository();

            // Test 1: Add a student
            int id1 = repo.add("Ada Lovelace");
            check("add returns generated id", id1 > 0);

            // Test 2: Find by id
            StudentRepository.Student found = repo.find(id1);
            check("find returns correct student", found != null && found.name().equals("Ada Lovelace"));

            // Test 3: Find non-existent
            StudentRepository.Student notFound = repo.find(99999);
            check("find returns null for missing id", notFound == null);

            // Test 4: Update
            int updated = repo.update(id1, "Augusta Ada King");
            check("update affects one row", updated == 1);

            StudentRepository.Student afterUpdate = repo.find(id1);
            check("update persists new name", afterUpdate.name().equals("Augusta Ada King"));

            // Test 5: Add another and findAll
            int id2 = repo.add("Grace Hopper");
            List<StudentRepository.Student> all = repo.findAll();
            check("findAll returns all students", all.size() >= 2);

            // Test 6: Search
            List<StudentRepository.Student> searched = repo.search("Ada");
            check("search finds matching students", searched.stream().anyMatch(s -> s.name().contains("Ada")));

            // Test 7: Delete
            int deleted = repo.delete(id2);
            check("delete affects one row", deleted == 1);

            StudentRepository.Student afterDelete = repo.find(id2);
            check("delete removes student", afterDelete == null);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("\n" + passed + " passed, " + failed + " failed.");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void check(String testName, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("PASS: " + testName);
        } else {
            failed++;
            System.out.println("FAIL: " + testName);
        }
    }
}
