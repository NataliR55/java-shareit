package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
/*
    User add(User user);

    User update(User user);

    List<User> findAll();

    User getUserById(long id);

    void delete(long userId);

    void deleteAll();

 */
}
