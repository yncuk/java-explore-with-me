package ru.practicum.services;


import ru.practicum.model.Category;

import java.util.List;

public interface CategoryService {
    Category create(Category category);

    Category patch(Integer catId, Category category);

    void delete(Integer catId);

    List<Category> findAll(Integer from, Integer size);

    Category findById(Integer catId);
}
