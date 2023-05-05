package ru.practicum.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.EntityBadRequestException;
import ru.practicum.model.Category;
import ru.practicum.repositories.CategoryRepository;
import ru.practicum.services.CategoryService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category patch(Integer catId, Category category) {
        validateCategoryId(catId);
        categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найдена категория с ID = %s", catId)));
        category.setId(catId);
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Integer catId) {
        validateCategoryId(catId);
        categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найдена категория с ID = %s", catId)));
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<Category> findAll(Integer from, Integer size) {
        return categoryRepository.findAll().stream().skip(from).limit(size).collect(Collectors.toList());
    }

    @Override
    public Category findById(Integer catId) {
        validateCategoryId(catId);
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найдена категория с ID = %s", catId)));
    }

    private void validateCategoryId(Integer catId) {
        if (catId <= 0) {
            throw new EntityBadRequestException("ID категории должен быть больше 0");
        }
    }
}
