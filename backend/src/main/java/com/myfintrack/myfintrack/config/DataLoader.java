package com.myfintrack.myfintrack.config;

import com.myfintrack.myfintrack.entity.Category;
import com.myfintrack.myfintrack.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        // Check if categories already exist
        if (categoryRepository.count() > 0) {
            log.info("✅ Categories already loaded. Skipping seed data.");
            return;
        }

        log.info("🌱 Seeding default categories...");

        List<Category> categories = Arrays.asList(
                // INCOME CATEGORIES
                Category.builder().name("Gaji").type(Category.TransactionType.INCOME).color("#10b981").build(),
                Category.builder().name("Freelance").type(Category.TransactionType.INCOME).color("#14b8a6").build(),
                Category.builder().name("Investasi").type(Category.TransactionType.INCOME).color("#06b6d4").build(),
                Category.builder().name("Bonus").type(Category.TransactionType.INCOME).color("#0ea5e9").build(),
                Category.builder().name("Hadiah").type(Category.TransactionType.INCOME).color("#3b82f6").build(),
                Category.builder().name("Lainnya").type(Category.TransactionType.INCOME).color("#6366f1").build(),

                // EXPENSE CATEGORIES
                Category.builder().name("Makanan & Minuman").type(Category.TransactionType.EXPENSE).color("#ef4444").build(),
                Category.builder().name("Transportasi").type(Category.TransactionType.EXPENSE).color("#f97316").build(),
                Category.builder().name("Belanja").type(Category.TransactionType.EXPENSE).color("#f59e0b").build(),
                Category.builder().name("Tagihan").type(Category.TransactionType.EXPENSE).color("#eab308").build(),
                Category.builder().name("Hiburan").type(Category.TransactionType.EXPENSE).color("#84cc16").build(),
                Category.builder().name("Kesehatan").type(Category.TransactionType.EXPENSE).color("#22c55e").build(),
                Category.builder().name("Pendidikan").type(Category.TransactionType.EXPENSE).color("#06b6d4").build(),
                Category.builder().name("Olahraga").type(Category.TransactionType.EXPENSE).color("#0ea5e9").build(),
                Category.builder().name("Kecantikan").type(Category.TransactionType.EXPENSE).color("#8b5cf6").build(),
                Category.builder().name("Hadiah & Donasi").type(Category.TransactionType.EXPENSE).color("#a855f7").build(),
                Category.builder().name("Rumah Tangga").type(Category.TransactionType.EXPENSE).color("#d946ef").build(),
                Category.builder().name("Asuransi").type(Category.TransactionType.EXPENSE).color("#ec4899").build(),
                Category.builder().name("Pajak").type(Category.TransactionType.EXPENSE).color("#f43f5e").build(),
                Category.builder().name("Perawatan Kendaraan").type(Category.TransactionType.EXPENSE).color("#fb7185").build(),
                Category.builder().name("Liburan").type(Category.TransactionType.EXPENSE).color("#fda4af").build(),
                Category.builder().name("Lainnya").type(Category.TransactionType.EXPENSE).color("#94a3b8").build()
        );

        categoryRepository.saveAll(categories);
        log.info("✅ Successfully seeded {} categories", categories.size());
    }
}
