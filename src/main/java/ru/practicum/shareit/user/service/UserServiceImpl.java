package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto add(User user) {
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(Long userId, User user) {
        getUserById(userId);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDto patchUpdate(long id, Map<String, String> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found", id)));
        if (!((updates.containsKey("name") || (updates.containsKey("email"))))) {
            throw new ValidationException("Update not have correct fields");
        }
        if (updates.containsKey("name")) {
            String name = updates.get("name");
            user.setName(name.trim());
        }
        if (updates.containsKey("email")) {
            String email = updates.get("email");
            user.setEmail(email.trim());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found", id)));
    }

    @Override
    public UserDto getUserDtoById(long id) {
        return UserMapper.toUserDto(getUserById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll() {
        return UserMapper.toUserDtoList(userRepository.findAll());
    }

    @Transactional
    @Override
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
