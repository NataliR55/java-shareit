package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserController userController;
/*

    @AfterEach
    public void clearAll() {
        userController.clearAllUser();
    }

    @Test
    public void getUsers() throws Exception {
        User[] users = {createUser(1), createUser(2), createUser(3), createUser(4)};
        assertEquals(userController.getAllUsers().size(), 0);
        for (User user : users) {
            mockMvc.perform(post("/users")
                    .content(objectMapper.writeValueAsString(user))
                    .contentType(MediaType.APPLICATION_JSON)
            );
        }
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
        assertThat(userController.getAllUsers().size()).isEqualTo(4);
        int id = userController.getAllUsers().get(2).getId();
        assertThat(userController.getUser(id).getEmail()).isEqualTo("user3@mail.ru");
    }


    private int getUsersId(int index) {
        return userController.getAllUsers().size() == 0 ? 1 : userController.getAllUsers().get(index).getId();
    }

    @Test
    public void postUser() throws Exception {
        User user = createUser(10);
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("NameUser10"));

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(null))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError());

        assertThat(userController.getAllUsers().size()).isEqualTo(1);

    }

    //TODO дописать тест
    @Test
    void testUpdateUser() throws Exception {

        // PATCH запрос
        mockMvc.perform(
                        patch("/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\": \"update\",\"email\": \"update@user.com\"}")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        // Так можно получить статус ответа
        int status = responsePost.getStatus();
    }

    @Test
    public void putUser() throws Exception {
        User user = createUser(1);
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        user = createUser(10);
        int id = getUsersId(0);
        user.setId(id);
        mockMvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("name").value("NameUser10"));
        assertEquals(userController.getAllUsers().size(), 1);
        assertEquals(userController.getUser(id).getName(), "NameUser10");
    }

    @Test
    public void checkEmail() throws Exception {
        User user = createUser(1);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        user = createUser(2);
        user.setEmail("user2#$$mail.ru");
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        assertEquals(userController.getAllUsers().size(), 1);
        user = createUser(3);
        user.setEmail("&user3#mail_ru");
        user.setId(1);
        mockMvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        user = createUser(4);
        user.setEmail("");
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        assertEquals(userController.getAllUsers().size(), 1);
        int id = getUsersId(0);
        assertEquals(userController.getUser(id).getEmail(), "user1@mail.ru");
    }

    @Test
    public void checkName() throws Exception {
        User user = createUser(1);
        user.setName("");
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        int id = getUsersId(0);
        assertEquals(userController.getUser(id).getName(), "login1");
    }

    private User createUser(int id) {
        String name = "user";
        String email = "user@user.com";
        return User.builder()
                .id(0)
                .email("user" + id + "@mail.ru")
                .name("NameUser" + id).build();
    }
*/
}
