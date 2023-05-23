package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserDto add(UserDto user);

    UserDto update(UserDto user);

    UserDto patchUpdate(long id, Map<String, String> updates);

    UserDto get(long id);

    List<UserDto> getAll();

    void delete(long userId);

    void deleteAll();

    void checkEmail(String email, long id);

    void checkName(String name, long id);

}
