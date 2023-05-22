package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User add(User user);

    User update(User user);

    List<User> findAll();

    User getUser(long id);

    void deleteUser(long userId);

    void deleteAllUser();
}
