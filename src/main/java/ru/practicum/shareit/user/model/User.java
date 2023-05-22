package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
public class User {
    private long id;
    @NotBlank
    @Pattern(regexp = "\\S+")
    private String name;
    @Email(message = "Field: Email must have the format EMAIL!")
    @NotBlank(message = "Field: Email must be filled!")
    private String email;

    public static class UserBuilder {
        private String email;
        private String name;

        public UserBuilder email(String email) {
            this.email = email.trim();
            return this;
        }

        public UserBuilder name(String name) {
            this.name = (name == null || name.isBlank()) ? email : name.trim();
            return this;
        }
    }

}
