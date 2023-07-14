package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validationGroup.Create;
import ru.practicum.shareit.validationGroup.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Name must be filled!")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Email must be filled!")
    @Email(groups = {Create.class, Update.class}, message = "Email must have the format  info@email.com!")
    private String email;
}