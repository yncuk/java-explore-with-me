package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
