package ru.practicum.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.EntityBadRequestException;
import ru.practicum.model.User;
import ru.practicum.repositories.UserRepository;
import ru.practicum.services.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getListUsers(Integer[] ids, Integer from, Integer size) {
        if (ids != null) {
            return userRepository.findAllByIds(ids).stream().skip(from).limit(size).collect(Collectors.toList());
        } else {
            return userRepository.findAll().stream().skip(from).limit(size).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void delete(Integer userId) {
        validateUserId(userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден пользователь с ID = %s", userId)));
        userRepository.deleteById(userId);
    }

    private void validateUserId(Integer userId) {
        if (userId <= 0) {
            throw new EntityBadRequestException("ID пользователя должен быть больше 0");
        }
    }
}
