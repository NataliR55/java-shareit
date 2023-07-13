package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor()
public class UserDto {
    private Long id;
    @NotBlank(message = "Name must be filled!")
    @Pattern(regexp = "\\S+")
    private String name;
    @Email(message = "Email must have the format EMAIL!")
    @NotBlank(message = "Email must be filled!")
    private String email;
}