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
    public User getUser(long id) {
        User user = users.get(id);
        if (user != null) {
            return user;
        }
        log.info("User with id:{} not exists.", id);
        throw new NotFoundException(String.format("User with id: %d is not exist", id));
    }

    @Override
    public void deleteUser(long userId) {
        getUser(userId);
        users.remove(userId);
    }

    @Override
    public void deleteAllUser() {
        lastId = 0;
        users.clear();
    }

    private long getNewId() {
        return users.keySet().stream()
                .max(Comparator.comparing(Long::longValue)).orElse(0L) + 1;
    }

    private User getById(Long id) {
        Optional<User> user = Optional.ofNullable(users.get(id));
        return user.orElseThrow(() -> new NotFoundException("User not found id= " + id));
    }
}
