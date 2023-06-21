package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = "Fild name must be filled")
    private String name;
    @NotBlank(message = "Fild description must be filled")
    private String description;
    @NotNull(message = "Fild available must be filled")
    private Boolean available;
    @JsonIgnore
    private User owner;
    @JsonIgnore
    private Long request;
    private BookingDtoShort lastBooking;
    private BookingDtoShort nextBooking;
    private List<CommentDto> comments;
}