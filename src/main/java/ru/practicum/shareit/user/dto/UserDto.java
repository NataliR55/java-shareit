package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = "Name must be filled!")
    @Pattern(regexp = "\\S+")
    private String name;
    @Email(message = "Email must have the format EMAIL!")
    @NotBlank(message = "Email must be filled!")
    private String email;
}