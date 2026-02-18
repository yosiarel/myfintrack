package com.myfintrack.myfintrack.repository;

import com.myfintrack.myfintrack.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByType(Category.TransactionType type);

    List<Category> findAllByOrderByTypeAscNameAsc();
}
