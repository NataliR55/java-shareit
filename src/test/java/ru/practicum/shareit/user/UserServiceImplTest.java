package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserService userService;

    @Mock
    UserRepository userRepository;

    @Test
    void add() {
    }

    @Test
    void update() {
    }

    @Test
    void patchUpdate() {
    }

    @Test
    void getUserById() {
    }

    @Test
    void getUserDtoById() {
    }

    @Test
    void getAll() {
    }

    @Test
    void addUser_whenUserEmailNotValid_thenNotSaveUser(){
//        User userTSave=new User();
//        doThrow(ValidationException.class)
//                .when(user)

    }

}