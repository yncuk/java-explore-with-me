package ru.practicum.services;

import ru.practicum.model.User;

import java.util.List;

public interface UserService {

    List<User> getListUsers(Integer[] ids, Integer from, Integer size);

    User create(User user);

    void delete(Integer userId);
}
