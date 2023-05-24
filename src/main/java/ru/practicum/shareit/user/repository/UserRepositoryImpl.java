package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
@Qualifier("userRepository")
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long lastId = 0L;

    @Override
    public User add(User user) {
        user.setId(++lastId);
        log.info("New user added: {}", user);
        users.put(lastId, user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.info("User updated {}", user);
        return user;
    }

    @Override
    public List<User> findAll() {
        log.info("Current number of users: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User get(long id) {
        User user = users.get(id);
        if (user != null) {
            return user;
        }
        log.info("User with id:{} not exists.", id);
        throw new NotFoundException(String.format("User with id: %d is not exist", id));
    }

    @Override
    public void delete(long userId) {
        get(userId);
        users.remove(userId);
    }

    @Override
    public void deleteAll() {
        lastId = 0;
        users.clear();
    }

}
