package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validationgroup.CreateGroup;
import ru.practicum.shareit.validationgroup.UpdateGroup;

import javax.validation.constraints.*;

@Data
@Builder
public class UserDto {
    @Null(groups = {CreateGroup.class})
    @NotNull(groups = {UpdateGroup.class})
    private Long id;
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    @Pattern(groups = {CreateGroup.class, UpdateGroup.class}, regexp = "\\S+")
    @Size(min = 3, max = 100)
    private String name;
    @Email(groups = {CreateGroup.class, UpdateGroup.class}, message = "Field: Email must have the format EMAIL!")
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "Field: Email must be filled!")
    @Size(min = 5, max = 100)
    private String email;
}