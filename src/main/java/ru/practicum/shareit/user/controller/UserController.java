package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto user) {
        log.info("{}", user);
        return userService.add(user);
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UserDto user) {
        return userService.update(user);
    }

    @PatchMapping("{id}")
    public UserDto partialUpdate(@PathVariable(required = true) long id, @RequestBody Map<String, String> updates) {
        return userService.partialUpdate(id, updates);
    }

    @GetMapping("{id}")
    public UserDto getUser(@PathVariable(required = false) long id) {
        return userService.get(id);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable long id) {
        userService.delete(id);
    }

}
