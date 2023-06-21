package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoInput {
    @NotNull
    private Long itemId;
    @NotNull(message = "Date must be filled")
    @FutureOrPresent(message = "Date must be filled")
    private LocalDateTime start;
    @NotNull(message = "Date must be filled")
    @Future(message = "Date must be filled")
    private LocalDateTime end;
}
