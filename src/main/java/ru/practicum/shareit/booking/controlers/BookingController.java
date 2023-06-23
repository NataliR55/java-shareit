package ru.practicum.shareit.booking.controlers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.services.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final String userIdInHeader = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOutput create(@RequestHeader(userIdInHeader) long userId,
                                   @Valid @RequestBody BookingDtoInput booking) {
        return bookingService.create(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutput update(@RequestHeader(userIdInHeader) Long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutput getById(@RequestHeader(userIdInHeader) Long userId, @PathVariable Long bookingId) {
        return bookingService.getBookingDtoById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoOutput> getBookingsOfUser(@RequestHeader(userIdInHeader) Long userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingsOfBooker(State.getState(state), userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoOutput> getBookingsOfOwner(@RequestHeader(userIdInHeader) Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingsOfOwner(State.getState(state), userId);
    }
}
