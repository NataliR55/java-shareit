package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InternalServerError;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exception.ValidationException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto add(User user) {
        checkEmail(user.getEmail(), -1L);
        checkName(user.getName(), -1L);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(User user) {
        long id = user.getId();
        getUserById(id);
        checkEmail(user.getEmail(), id);
        checkName(user.getName(), id);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDto patchUpdate(long id, Map<String, String> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found", id)));
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
        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto getUserById(long id) {
        return UserMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found", id))));
    }

    @Override
    public List<UserDto> getAll() {
        return UserMapper.toUserDtoList(userRepository.findAll());
    }

    @Transactional
    @Override
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void checkEmail(String email, long id) {
        if ((email == null) || (email.isBlank())) {
            log.info("User email empty!");
            throw new ValidationException("User email is empty!");
        }
        String foundEmail = email.trim();
        Optional<User> userFound = userRepository.findUsersByEmailEqualsIgnoreCase(foundEmail).stream()
                .filter(t -> t.getId() != id)
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
        String foundName = name.trim();
        Optional<User> userFound = userRepository.findUsersByNameEqualsIgnoreCase(foundName).stream()
                .filter(t -> t.getId() != id)
                .findFirst();
        if (userFound.isPresent()) {
            log.info("User with name {} already exist!", name);
            throw new InternalServerError(String.format("User with name %s already exist!", name));
        }
    }
    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }

}
