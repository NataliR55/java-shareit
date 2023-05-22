package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InternalServerError;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto add(UserDto user) {
        if (user == null) {
            log.info("You try add null User.");
            throw new ValidationException("You try add null User.");
        }
        checkEmail(user.getEmail(), -1L);
        checkName(user.getName(), -1L);
        User newUser = userRepository.add(UserMapper.mapToUser(user));
        return UserMapper.mapToUserDto(newUser);
    }

    @Override
    public UserDto update(UserDto user) {
        if (user == null) {
            log.info("User is null!");
            throw new ValidationException("User is null!");
        }
        long id = user.getId();
        get(id);
        checkEmail(user.getEmail(), id);
        checkName(user.getName(), id);
        User userUpdate = userRepository.update(UserMapper.mapToUser(user));
        return UserMapper.mapToUserDto(userUpdate);
    }

    @Override
    public UserDto partialUpdate(long id, Map<String, String> updates) {
        User user = userRepository.getUser(id);
        if (updates.containsKey("name")) {
            String name = updates.get("name");
            checkName(name, id);
            user.setName(name.trim());
        }
        if (updates.containsKey("email")) {
            String email = updates.get("email");
            checkEmail(email, id);
            user.setEmail(email.trim());
        }
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto get(long id) {
        return UserMapper.mapToUserDto(userRepository.getUser(id));
    }

    @Override
    public List<UserDto> findAll() {
        return UserMapper.mapToUsersDto(userRepository.findAll());
    }

    @Override
    public void delete(long userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public void checkEmail(String email, long id) {
        if ((email == null) || (email.isBlank())) {
            log.info("User email empty!");
            throw new ValidationException("User email is empty!");
        }
        String foundEmail = email.trim().toLowerCase();
        //.filter(t -> foundEmail.equals(t.getEmail().toLowerCase()))
        Optional<User> userFound = userRepository.findAll().stream()
                .filter(t -> {
                    return t.getId() != id;
                })
                .filter(t -> foundEmail.equals(t.getEmail().toLowerCase()))
                .findFirst();
        if (userFound.isPresent()) {
            log.info("User with email {} already exist!", email);
            throw new InternalServerError(String.format("User with login %s already exist!", email));
        }
    }

    @Override
    public void checkName(String name, long id) {
        if ((name == null) || (name.isBlank())) {
            log.info("User name empty!");
            throw new ValidationException("User name is empty!");
        }
        String foundName = name.trim().toLowerCase();
        Optional<User> userFound = userRepository.findAll().stream()
                .filter(t -> {
                    return t.getId() != id;
                })
                .filter(t -> foundName.equals(t.getEmail().toLowerCase()))
                .findFirst();
        if (userFound.isPresent()) {
            log.info("User with name {} already exist!", name);
            throw new InternalServerError(String.format("User with name %s already exist!", name));
        }
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAllUser();
    }

}
