package ru.practicum.shareit.user;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    private final EasyRandom generator = new EasyRandom();

    @Test
   @DirtiesContext
    void findUsersByEmailEqualsIgnoreCase() {
        String email1 = "user1@mail.ru";
        String name1 = "user1";
        userRepository.save(User.builder().email(email1).name(name1).build());
        userRepository.save(User.builder().email("user2@mail.ru").name("user2").build());
        User user = generator.nextObject(User.class);
        userRepository.save(user);
        List<User> users = userRepository.findUsersByEmailEqualsIgnoreCase(email1);
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getName(), name1);
        assertEquals(users.get(0).getId(), 1L);
    }

    @Test
    @DirtiesContext
    void test_findUsersByNameEqualsIgnoreCase() {
        String email1 = "user1@mail.ru";
        String name1 = "user1";
        userRepository.save(User.builder().email(email1).name(name1).build());
        userRepository.save(User.builder().email("user2@mail.ru").name("user2").build());
        List<User> users = userRepository.findUsersByNameEqualsIgnoreCase(name1);
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getName(), name1);
    }
}