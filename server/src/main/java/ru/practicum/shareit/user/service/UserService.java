package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto add(User user);

    UserDto update(Long userId, UserDto userDto);

    User getUserById(long id);

    UserDto getUserDtoById(long id);

    List<UserDto> getAll();

    void delete(long userId);

    void deleteAll();

}
