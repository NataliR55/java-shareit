package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Data
public class InputItemRequestDto {
    @NotBlank
    private String description;
}
