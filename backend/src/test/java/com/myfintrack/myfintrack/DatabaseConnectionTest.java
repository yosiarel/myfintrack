package com.myfintrack.myfintrack;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestSecurityConfig.class)
class DatabaseConnectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testDatabaseConnection() {
        // Test koneksi database dengan query sederhana
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

        assertNotNull(result);
        assertEquals(1, result);

        System.out.println("✅ Database connection successful!");
    }

    @Test
    void testTablesExist() {
        // Cek apakah tabel users ada
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'users'",
                Integer.class);

        assertEquals(1, count);
        System.out.println("✅ Table 'users' exists!");

        // Cek semua tabel
        Integer totalTables = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public'",
                Integer.class);

        System.out.println("📊 Total tables in database: " + totalTables);
        assertTrue(totalTables >= 4, "Should have at least 4 tables");
    }

    @Test
    void testCategoriesData() {
        // Cek apakah ada data kategori
        Integer categoryCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM categories",
                Integer.class);

        System.out.println("📋 Total categories: " + categoryCount);
        assertEquals(22, categoryCount, "Should have 22 default categories");
    }
}
