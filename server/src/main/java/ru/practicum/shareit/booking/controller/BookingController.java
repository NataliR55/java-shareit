package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private static final String USER_ID_IN_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public OutputBookingDto create(@RequestHeader(USER_ID_IN_HEADER) long userId,
                                   @RequestBody InputBookingDto booking) {
        return bookingService.create(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public OutputBookingDto approveBooking(@RequestHeader(USER_ID_IN_HEADER) Long userId,
                                           @PathVariable Long bookingId,
                                           @RequestParam boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public OutputBookingDto getById(@RequestHeader(USER_ID_IN_HEADER) Long userId, @PathVariable Long bookingId) {
        return bookingService.getBookingDtoById(bookingId, userId);
    }

    @GetMapping
    public List<OutputBookingDto> getBookingsOfBooker(@RequestHeader(USER_ID_IN_HEADER) Long userId,
                                                      @RequestParam(required = false) String state,
                                                      @RequestParam(required = false) int from,
                                                      @RequestParam(required = false) int size) {
        return bookingService.getBookingsOfBooker(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<OutputBookingDto> getBookingsOfOwner(@RequestHeader(USER_ID_IN_HEADER) Long userId,
                                                     @RequestParam(required = false) String state,
                                                     @RequestParam(required = false) int from,
                                                     @RequestParam(required = false) int size) {
        return bookingService.getBookingsOfOwner(state, userId, from, size);
    }
}
