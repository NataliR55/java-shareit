package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validationGroup.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor()
public class UserDto {
    private Long id;
    @NotBlank(groups = Create.class, message = "Name must be filled!")
    @Pattern(regexp = "\\S+")
    private String name;
    @NotBlank(groups = Create.class, message = "Email must be filled!")
    @Email(message = "Email must have the format EMAIL!")
    private String email;
}